jQuery.sap.require("com.sap.hana.cloud.samples.benefits.util.AjaxUtil");

sap.ui
		.controller(
				"com.sap.hana.cloud.samples.benefits.view.orders.Details",
				{
					onInit : function() {
						this.getView().setModel(new sap.ui.model.json.JSONModel());

						sap.ui.getCore().getEventBus().subscribe("app", "ordersDetailsRefresh", this._refreshHandler, this);
						sap.ui.getCore().getEventBus().subscribe("dataLoad", "benefitInfo", this.selectFirstBenefit, this);

						var orderItemsList = this.getView().byId("orderItemsList");

						orderItemsList.addEventDelegate({

							onAfterRendering : function(e) {
								$("[id$=-imgDel]").attr('tabindex', 0);
								$("[id$=-imgDel]").addClass("itemFocus");
							}
						});
					},
					onBeforeRendering : function() {
						this.hideLogout();
					},
					initEmployeeDetailsModel : function(employeeProfile, campaignId, activeCampaign) {
						this.getView().getModel().setData(null);
						this.employeeProfile = employeeProfile;
						this.campaignId = campaignId;
						this.activeCampaign = activeCampaign;

						var orderDeferred = this.loadOrderDetails();
						orderDeferred.done(jQuery.proxy(function() {
							var availablePointsDeferred = this._loadUserAvailablePoints(campaignId, employeeProfile.UserId);
							var entitlementPointsDeferred = this._loadUserTargetPoints(campaignId, employeeProfile.UserId);

							var alwaysFunc = jQuery.proxy(function() {
								this.getView().setBusy(false);
								this._determineAddButtonState();
							}, this);

							jQuery.when(availablePointsDeferred, entitlementPointsDeferred).always(alwaysFunc);
						}, this));
					},

					loadOrderDetails : function() {
						var doneCallback = function(data, textStatus, jqXHR) {
							if (!data.d.results[0]) {
								data.d.results[0] = {
									"Total" : 0
								};
							}

							this.getView().getModel().setData({
								employee : this.employeeProfile,
								currentOrder : data,
								activeCampaign : this.activeCampaign
							});
						};

						var failCallback = function() {
							sap.m.MessageBox.show(sap.ui.getCore().getModel("b_i18n").getProperty("EMPLOYEE_ORDERS_FAILED")
									.formatPropertyMessage(this.employeeProfile.UserId), sap.m.MessageBox.Icon.ERROR,
									"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK]);
						};

						var alwaysCallback = function() {
							this.getView().setBusy(false);
							this._determineAddButtonState();
						};

						this.getView().setBusy(true);

						var requestUrl = "OData.svc/Orders?$expand=OrderDetailsDetails/BenefitTypeDetails/BenefitInfoDetails,CampaignDetails&$filter=CampaignId%20eq%20"
								+ this.campaignId + "%20and%20UserId%20eq%20" + this.employeeProfile.Id;
						return com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, requestUrl, doneCallback,
								failCallback, alwaysCallback);
					},

					_determineAddButtonState : function() {
						this.enableAddBtn(!!this.getView().getModel().getData()
								&& !!(this.employeeProfile && this.employeeProfile.targetPoints)
								&& !!(this.activeCampaign && this.activeCampaign.Active && this.activeCampaign.Points)
								&& jQuery.isNumeric(this.getView().getModel().getData().currentOrder.d.results[0].Total));
					},

					enableAddBtn : function(bValue) {
						this.byId("addButton").setEnabled(bValue);
					},

					_loadUserAvailablePoints : function(campaignId, userId) {
						var doneCallback = function(data, textStatus, jqXHR) {
							var points = data && data.d && data.d.AvailablePoints;
							this.getView().getModel().setProperty("/activeCampaign/Points", points ? points : null);
						};

						var failCallback = function() {
							sap.m.MessageBox.alert("{b_i18n>FAILED_USER_POINT_QUERY}");
						};

						this.getView().setBusy(true);

						var requestUrl = "OData.svc/userPoints?userId='" + userId + "'&campaignId=" + campaignId;

						return com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, requestUrl, doneCallback,
								failCallback);
					},

					_loadUserTargetPoints : function(campaignId, userId) {
						var doneCallback = function(data, textStatus, jqXHR) {
							var points = data && data.d && data.d.BenefitsAmount && data.d.BenefitsAmount.targetPoints;
							this.getView().getModel().setProperty("/employee/targetPoints", points ? points : null);
						};

						var failCallback = function() {
							sap.m.MessageBox.alert("{b_i18n>FAILED_USER_TARGET_POINTS_QUERY}");
						};

						this.getView().setBusy(true);

						return com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this,
								"OData.svc/BenefitsAmount?userId='" + userId + "'", doneCallback, failCallback);
					},

					addItem : function() {
						jQuery.sap.require("sap.m.MessageToast");
						if (!this.addItemDialog) {
							this.addItemDialog = sap.ui.xmlfragment("addItemDialog", "view.orders.addItemDialog", this);
						}
						if (!this.addItemDialog.getModel("benefitsModel")) {
							this.addItemDialog.setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
						}
						var dialog = this.addItemDialog;

						var doneCallback = function(data, textStatus, jqXHR) {
							dialog.getModel("benefitsModel").setData(data);
							window.setTimeout(function() {
								sap.ui.getCore().getEventBus().publish("dataLoad", "benefitInfo", {
									sourceControl : this
								});
							}, 0);
						};

						var failCallback = function() {
							sap.m.MessageBox.show("{b_i18n>BENEFITS_DETAILS_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR,
									"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK], function(oAction) {
										dialog.close();
									});
						};

						var alwaysCallback = function() {
							dialog.setBusy(false);
						};

						dialog.open();
						dialog.setBusy(true);

						com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this,
								"OData.svc/BenefitInfos?$expand=BenefitTypeDetails", doneCallback, failCallback, alwaysCallback);
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
						var hasValidValues = !!(targetPoints && jQuery.isNumeric(orderPrice));
						return avPointsMsgTemplate.formatPropertyMessage(hasValidValues ? (targetPoints - orderPrice) : "");
					},
					formatUsedPoints : function(usedPoints) {
						return usedPoints ? usedPoints : "";
					},
					formatBenefitPointsEntitlement : function(points) {
						return sap.ui.getCore().getModel("b_i18n").getProperty("ALL_BENEFIT_POINTS").formatPropertyMessage(
								points ? points : "");
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
					selectFirstBenefit : function(oCtrEvt) {
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

							var doneCallback = function() {
								dialog.close();
								sap.m.MessageToast.show(sap.ui.getCore().getModel("b_i18n").getProperty("ORDER_ACCEPTED_MSG"));
								this.fireModelChange();
								this.loadOrderDetails();
							};

							var failCallback = function() {
								sap.m.MessageBox.show("{b_i18n>ORDER_CREATION_FAILED}", sap.m.MessageBox.Icon.ERROR,
										"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK]);
							};

							var alwaysCallback = function() {
								dialog.setBusy(false);
							};

							dialog.setBusy(true);
							com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchPostJSON(this, addServiceURL, null, doneCallback,
									failCallback, alwaysCallback);
						} else {
							dialog.close();
							sap.m.MessageBox.alert(sap.ui.getCore().getModel("b_i18n").getProperty("LIMIT_EXCEEDED_MSG"));
						}
					},
					_deleteOrder : function(orderId) {
						var doneCallback = function() {
							this.fireModelChange();
							this.loadOrderDetails();
						};

						var failCallback = function() {
							sap.m.MessageBox.show("{b_i18n>ORDER_DELETION_FAILED}", sap.m.MessageBox.Icon.ERROR,
									"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK]);
						};

						var alwaysCallback = function() {
							appController.setAppBusy(false);
						};

						appController.setAppBusy(true);
						com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchDelete(this, "OData.svc/deleteOrder?orderId="
								+ orderId, doneCallback, failCallback, alwaysCallback);
					},
					_setControlBindCtx : function(control, ctx) {
						control.setBindingContext(ctx);
						control.setModel(ctx.getModel());
					}
				});
