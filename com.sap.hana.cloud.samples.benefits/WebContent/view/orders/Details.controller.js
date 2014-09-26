sap.ui
		.controller(
				"com.sap.hana.cloud.samples.benefits.view.orders.Details",
				{
					onInit : function() {
						this.getView().setModel(new sap.ui.model.json.JSONModel());
						sap.ui.getCore().getEventBus().subscribe("app", "ordersDetailsRefresh", this._refreshHandler, this);

						var orderItemsList = this.getView().byId("orderItemsList");

						orderItemsList.addEventDelegate({

							onAfterRendering : function(e) {
								$("[id$=-imgDel]").attr('tabindex', 0);
								$("[id$=-imgDel]").addClass("itemFocus");
							}
						});
					},

					onBeforeRendering : function() {
						this.loadBenefitsModel();

						this.hideLogout();
					},
					initEmployeeDetailsModel : function(employeeProfile, campaignId, activeCampaign) {
						this.employeeProfile = employeeProfile;
						this.campaignId = campaignId;
						this.activeCampaign = activeCampaign;
						this.loadOrderDetails();
						this._loadAvailablePointsToModel(campaignId, employeeProfile.UserId);
						this._loadEntitlementPointsToModel(campaignId, employeeProfile.UserId);
					},

					loadOrderDetails : function() {
						var requestUrl = "OData.svc/Orders?$expand=OrderDetailsDetails/BenefitTypeDetails/BenefitInfoDetails,CampaignDetails&$filter=CampaignId%20eq%20"
								+ this.campaignId + "%20and%20UserId%20eq%20" + this.employeeProfile.Id;
						var orderDetails = jQuery.sap.syncGetJSON(requestUrl).data;

						this.getView().getModel().setData({
							employee : this.employeeProfile,
							currentOrder : orderDetails,
							activeCampaign : this.activeCampaign
						});
						this.byId("addButton").setEnabled(this.activeCampaign.Active);
					},

					_loadAvailablePointsToModel : function(campaignId, userId) {
						var availablePoints = this._loadUserAvailablePoints(campaignId, userId);
						this.getView().getModel().setProperty("/activeCampaign/Points", availablePoints);
					},

					_loadUserAvailablePoints : function(campaignId, userId) {
						var result = {
							points : null
						};
						var successFunc = function(result) {
							return function(data, textStatus, jqXHR) {
								result.points = data.d.AvailablePoints;
							};
						};
						this.getView().setBusy(true);

						jQuery.ajax({
							async : false,
							url : "OData.svc/userPoints?userId='" + userId + "'&campaignId=" + campaignId,
							type : 'GET',
							dataType : 'json',
							success : successFunc(result),
							error : function(xhr, error) {
								var alertMsg = sap.ui.getCore().getModel("b_i18n").getProperty("FAILED_USER_POINT_QUERY");
								sap.m.MessageBox.alert(alertMsg);
							},
							complete : jQuery.proxy(function() {
								this.getView().setBusy(false);
							}, this)
						});
						return result.points;
					},

					_loadEntitlementPointsToModel : function(campaignId, userId) {
						var targetPoints = this._loadUserTargetPoints(campaignId, userId);
						this.getView().getModel().setProperty("/employee/targetPoints", targetPoints);
					},

					_loadUserTargetPoints : function(campaignId, userId) {
						this.getView().setBusy(true);

						var path = "OData.svc/BenefitsAmount?userId='" + userId + "'";
						var resultData = com.sap.hana.cloud.samples.benefits.util.Helper.synchGetJSON(path, function(xhr, error) {
						}, function(xhr, error) {
							var alertMsg = sap.ui.getCore().getModel("b_i18n").getProperty("FAILED_USER_POINT_QUERY");
							sap.m.MessageBox.alert(alertMsg);
						}, jQuery.proxy(function() {
							this.getView().setBusy(false);
						}, this));

						return resultData.d.BenefitsAmount.targetPoints;
					},

					loadBenefitsModel : function() {
						if (!this.getView().getModel("benefitsModel")) {
							this.getView().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
						}
						this.getView().getModel("benefitsModel").loadData("OData.svc/BenefitInfos?$expand=BenefitTypeDetails",
								null, false);
					},

					addItem : function() {
						jQuery.sap.require("sap.m.MessageToast");
						if (!this.addItemDialog) {
							this.addItemDialog = sap.ui.xmlfragment("addItemDialog", "view.orders.addItemDialog", this);
						}
						if (!this.addItemDialog.getModel("benefitsModel")) {
							this.addItemDialog.setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
						}
						this.addItemDialog.getModel("benefitsModel").loadData("OData.svc/BenefitInfos?$expand=BenefitTypeDetails",
								null, false);

						this.addItemDialog.open();
					},
					resetDialog : function() {
						sap.ui.getCore().byId("addItemDialog--quantityTypeTxt").setValue(null);
						sap.ui.getCore().byId("addItemDialog--quantityTypeTxt").setValueState(sap.ui.core.ValueState.None);
					},
					cancelButtonPressed : function() {
						this.addItemDialog.close();
						sap.m.MessageToast.show(sap.ui.getCore().getModel("b_i18n").getProperty("ORDER_CANCELED"));
					},
					formatPageTitle : function(firstName, lastName) {
						var pageTitleMsg = sap.ui.getCore().getModel("b_i18n").getProperty("ORDERS_DETAILS_PAGE_NAME")
								.formatPropertyMessage(firstName, lastName);
						return pageTitleMsg;
					},
					formatAvailablePoints : function(targetPoints, orderPrice) {
						var avPointsMsgTemplate = sap.ui.getCore().getModel("b_i18n").getProperty("LEFT_TO_USE_POINTS");
						return avPointsMsgTemplate.formatPropertyMessage(targetPoints
								- (jQuery.isNumeric(orderPrice) ? orderPrice : 0));
					},
					formatUsedPoints : function(usedPoints) {
						return usedPoints ? usedPoints : 0;
					},
					formatBenefitPointsEntitlement : function(points) {
						var entitlementMsg = sap.ui.getCore().getModel("b_i18n").getProperty("ALL_BENEFIT_POINTS")
								.formatPropertyMessage(points);
						return entitlementMsg;
					},
					formatItemValue : function(value) {
						var itemValueMsg = sap.ui.getCore().getModel("b_i18n").getProperty("ITEM_VALUE").formatPropertyMessage(
								value);
						return itemValueMsg;
					},
					formatTotalPoints : function(quantity, itemValue) {
						var total = quantity * itemValue;
						var totalPointsMsg = sap.ui.getCore().getModel("b_i18n").getProperty("TOTAL_ITEM_VALUE")
								.formatPropertyMessage(quantity, total);
						return totalPointsMsg;
					},
					linkPressed : function(evt) {
						var sourceControl = evt.getSource();
						var model = sourceControl.getModel();
						var contextPath = sourceControl.getBindingContext().sPath + "/benefitDetails/infoLink";
						var link = model.getProperty(contextPath);
						sap.m.URLHelper.redirect(link, true);
					},
					onBenefitSelect : function(evt) {
						var ctx = evt.getParameters().selectedItem.getBindingContext("benefitsModel");
						this._setControlBindCtx(sap.ui.getCore().byId("addItemDialog--quantityTypeSelect"), ctx);
						var selectedItem = sap.ui.getCore().byId("addItemDialog--quantityTypeSelect").getSelectedItem();
						var benefitsTypeCtx = selectedItem.getBindingContext();
						this._setControlBindCtx(sap.ui.getCore().byId("addItemDialog--priceTypeTxt"), benefitsTypeCtx);
						this.onValueSelect();

					},
					onQuantityTypeSelect : function(evt) {
						var ctx = evt.getParameters().selectedItem.getBindingContext();
						this._setControlBindCtx(sap.ui.getCore().byId("addItemDialog--priceTypeTxt"), ctx);
						this.onValueSelect();
					},
					onValueSelect : function() {
						var valueSelected = sap.ui.getCore().byId("addItemDialog--quantityTypeTxt").getValue();
						var itemValue = sap.ui.getCore().byId("addItemDialog--priceTypeTxt").getValue();
						var totalValue = valueSelected * itemValue;
						sap.ui.getCore().byId("addItemDialog--totalTypeTxt").setValue(totalValue != 0 ? totalValue : "0");
					},
					handleDialogOpen : function(oCtrEvt) {
						var typeSelector = sap.ui.getCore().byId("addItemDialog--quantityTypeSelect");
						var ctx = sap.ui.getCore().byId("addItemDialog--benefitTypeSelect").getSelectedItem().getBindingContext(
								"benefitsModel");
						typeSelector.setBindingContext(ctx);
						typeSelector.setModel(ctx.getModel());
						var item = typeSelector.getItems()[0];
						if (item) {
							typeSelector.setSelectedItem(item);
							var benefitsTypeCtx = item.getBindingContext();
							this._setControlBindCtx(sap.ui.getCore().byId("addItemDialog--priceTypeTxt"), benefitsTypeCtx);
							sap.ui.getCore().byId("addItemDialog--totalTypeTxt").setValue("0");
							this.closeBenefitSelectors();
						}
						sap.ui.getCore().byId("addItemDialog--quantityTypeTxt").focus();
					},
					onDelete : function(evt) {
						jQuery.sap.require("sap.m.MessageBox");
						var ctx = evt.getParameter("listItem").getBindingContext().getObject();
						var itemId = ctx.Id;
						var itemDetails = ctx.BenefitTypeDetails.Name + " " + ctx.Quantity + " x " + ctx.BenefitTypeDetails.Value
								+ " points";
						var onDeleteMsg = sap.ui.getCore().getModel("b_i18n").getProperty("DELETE_ORDER_MSG")
								.formatPropertyMessage(itemDetails);
						sap.m.MessageBox.confirm(onDeleteMsg, jQuery.proxy(function(action) {
							if (action === sap.m.MessageBox.Action.OK) {
								this._deleteOrder(itemId);
							}
						}, this));
					},
					closeBenefitSelectors : function() {
						sap.ui.getCore().byId("addItemDialog--benefitTypeSelect").close();
						sap.ui.getCore().byId("addItemDialog--quantityTypeSelect").close();
					},
					fireModelChange : function() {
						sap.ui.getCore().getEventBus().publish("refresh", "orders", {
							sourceId : this.getView().getId()
						});
					},
					hideLogout : function() {
						this.byId("logoutButton").setVisible(appController._hasLogoutButton());
					},
					logoutButtonPressed : function(evt) {
						sap.ui.getApplication().onLogout();
					},
					_refreshHandler : function(channelId, eventId, data) {
						this.initEmployeeDetailsModel(data.context.employee, data.context.campaignId, data.context.activeCampaign);
					},
					_addItem : function() {
						jQuery.sap.require("sap.m.MessageBox");

						var userEntitlementPoints = this.getView().getModel().getProperty("/employee").targetPoints;
						var currentOrder = this.getView().getModel().getProperty("/currentOrder").d.results[0];
						var availablePoints = userEntitlementPoints - (currentOrder ? currentOrder.Total : 0);
						var dialog = this.addItemDialog;
						var selItemVal = sap.ui.getCore().byId("addItemDialog--quantityTypeSelect").getSelectedItem()
								.getBindingContext().getObject().Value;
						var quantity = sap.ui.getCore().byId("addItemDialog--quantityTypeTxt").getValue();
						var value = selItemVal * quantity;

						if (value <= 0) {
							var alertMsg = sap.ui.getCore().getModel("b_i18n").getProperty("INCORRECT_QUANTITY_MSG");
							sap.m.MessageBox.alert(alertMsg);
						} else if (value <= availablePoints) {
							var benefitTypeId = sap.ui.getCore().byId("addItemDialog--quantityTypeSelect").getSelectedItem().getKey();
							var addServiceURL = "OData.svc/addOrder?campaignId=" + this.campaignId + "&userId='"
									+ this.employeeProfile.UserId + "'&quantity=" + quantity + "&benefitTypeId=" + benefitTypeId;
							jQuery.ajax({
								url : addServiceURL,
								type : 'post',
								dataType : 'json',
								success : jQuery.proxy(function(data) {
									dialog.close();
									sap.m.MessageToast.show(sap.ui.getCore().getModel("b_i18n").getProperty("ORDER_ACCEPTED_MSG"));
									this.fireModelChange();
									this.loadOrderDetails();
								}, this),
								error : function(xhr, error) {
									sap.m.MessageToast.show(xhr.responseText);
								}
							});
						} else {
							dialog.close();
							sap.m.MessageBox.alert(sap.ui.getCore().getModel("b_i18n").getProperty("LIMIT_EXCEEDED_MSG"));
						}
					},
					_deleteOrder : function(orderId) {
						appController.setAppBusy(true);
						jQuery.ajax({
							url : 'OData.svc/deleteOrder?orderId=' + orderId,
							type : 'delete',
							success : jQuery.proxy(function(data) {
								this.fireModelChange();
								this.loadOrderDetails();
							}, this),
							complete : jQuery.proxy(function() {
								appController.setAppBusy(false);
							}, this)
						});
					},
					_setControlBindCtx : function(control, ctx) {
						control.setBindingContext(ctx);
						control.setModel(ctx.getModel());
					}
				});
