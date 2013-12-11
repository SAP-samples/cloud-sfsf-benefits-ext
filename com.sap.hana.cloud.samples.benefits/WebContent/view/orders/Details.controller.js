sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Details", {
	onInit : function() {
		this.getView().setModel(new sap.ui.model.json.JSONModel());
		sap.ui.getCore().getEventBus().subscribe("app", "ordersDetailsRefresh", this._refreshHandler, this);
	},
	onBeforeRendering : function() {
		this.loadBenefitsModel();
	},
	initEmployeeDetailsModel : function(employeeProfile, campaignId) {
		this.employeeProfile = employeeProfile;
		this.campaignId = campaignId;
		this.loadOrderDetails();
		
	},
	loadOrderDetails : function() {
		var orderDetails = jQuery.sap.syncGetJSON("api/orders/for-user/" + this.campaignId + "/" + this.employeeProfile.userId).data;
		this.byId("addButton").setEnabled(orderDetails.campaign.active);
		
		this.getView().getModel().setData({
			employee : this.employeeProfile,
			currentOrder : orderDetails
		});
	},
	loadBenefitsModel : function() {
		if (!this.getView().getModel("benefitsModel")) {
			this.getView().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
		}
		this.getView().getModel("benefitsModel").loadData("api/benefits/all", null, false);
	},
	onAfterRendering : function() {
		
	},
	addItem : function() {
		jQuery.sap.require("sap.m.MessageToast");
		var dialog = this.byId("addItemCtrl");
		this.byId("quantityTypeTxt").setValue(null); 
		this.byId("quantityTypeTxt").setValueState(sap.ui.core.ValueState.None); 
		
		dialog.setLeftButton(new sap.m.Button({
			text : sap.ui.getCore().getModel("b_i18n").getProperty("OK_BTN_NAME"),
			press : jQuery.proxy(this._addItem, this)

		}));

		dialog.setRightButton(new sap.m.Button({
			text : sap.ui.getCore().getModel("b_i18n").getProperty("CANCEL_BTN_NAME"),
			press : function() {
				dialog.close();
				sap.m.MessageToast.show(sap.ui.getCore().getModel("b_i18n").getProperty("ORDER_CANCELED"));
			}
		}));

		dialog.open();
	},
	formatPageTitle : function(firstName, lastName) {
		var pageTitleMsg = sap.ui.getCore().getModel("b_i18n").getProperty("ORDERS_DETAILS_PAGE_NAME").formatPropertyMessage(firstName, lastName);
		return pageTitleMsg;
	},
	formatBenefitItemsSum : function(benefitItems) {
		var result = 0;
		for (benefitItem in benefitItems) {
			result += benefitItems[benefitItem].quantity * benefitItems[benefitItem].itemValue;
		}
		return result;
	},
	formatAvailablePoints : function(campaignPoints, usedPoints) {
		var result = campaignPoints - usedPoints;
		var avilablePointsMsg = sap.ui.getCore().getModel("b_i18n").getProperty("LEFT_TO_USE_POINTS").formatPropertyMessage(result.toString(10));
		return avilablePointsMsg;
	},
	formatBenefitPointsEntitlement : function(points) {
		var entitlementMsg = sap.ui.getCore().getModel("b_i18n").getProperty("ALL_BENEFIT_POINTS").formatPropertyMessage(points);
		return entitlementMsg;
	},
	formatItemValue : function(value) {
		var itemValueMsg = sap.ui.getCore().getModel("b_i18n").getProperty("ITEM_VALUE").formatPropertyMessage(value);
		return itemValueMsg;
	},
	formatTotalPoints : function(quantity, itemValue){
		var total= quantity*itemValue;
		var totalPointsMsg = sap.ui.getCore().getModel("b_i18n").getProperty("TOTAL_ITEM_VALUE").formatPropertyMessage(quantity, total);
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
		this._setControlBindCtx(this.byId("quantityTypeSelect"), ctx);
		var benefitsTypeCtx = this.byId("quantityTypeSelect").getSelectedItem().getBindingContext();
		this._setControlBindCtx(this.byId("priceTypeTxt"), benefitsTypeCtx);
		this.onValueSelect();
		
	},
	onQuantityTypeSelect : function(evt){
		var ctx = evt.getParameters().selectedItem.getBindingContext();
		this._setControlBindCtx(this.byId("priceTypeTxt"), ctx);
		this.onValueSelect();
	},
	onValueSelect : function(){
		var valueSelected = this.byId("quantityTypeTxt").getValue();
		var itemValue = this.byId("priceTypeTxt").getValue();
		var totalValue= valueSelected*itemValue;
		if(totalValue != 0){
			this.byId("totalTypeTxt").setValue(totalValue);
		} else {
			this.byId("totalTypeTxt").setValue("0");
		}
	},
	handleDialogOpen : function(oCtrEvt) {
		var typeSelector = this.byId("quantityTypeSelect");
		var ctx = this.byId("benefitTypeSelect").getSelectedItem().getBindingContext("benefitsModel");
		typeSelector.setBindingContext(ctx);
		typeSelector.setModel(ctx.getModel());
		var item = typeSelector.getItems()[0];
		if(item){
			typeSelector.setSelectedItem(item);
			var benefitsTypeCtx = item.getBindingContext();
			this._setControlBindCtx(this.byId("priceTypeTxt"), benefitsTypeCtx);
			this.byId("totalTypeTxt").setValue("0");
		}
	},
	onDelete : function(evt) {
		jQuery.sap.require("sap.m.MessageBox");
		var ctx = evt.getParameter("listItem").getBindingContext().getObject();
		var itemId = ctx.id;
		var itemDetails = ctx.name + " " + ctx.quantity + " x " + ctx.itemValue + " points";
	  var onDeleteMsg = sap.ui.getCore().getModel("b_i18n").getProperty("DELETE_ORDER_MSG").formatPropertyMessage(itemDetails);
		sap.m.MessageBox.confirm(onDeleteMsg, jQuery.proxy(function(action) {
			if (action === sap.m.MessageBox.Action.OK) {
				this._deleteOrder(itemId);
			}
		}, this));
	},
	closeDateTimeSelectors : function() {
		this.byId("benefitTypeSelect").close();
		this.byId("quantityTypeSelect").close();
	},
	fireModelChange : function() {
		sap.ui.getCore().getEventBus().publish("refresh", "orders", {
			sourceId : this.getView().getId()
		});
	},
	_refreshHandler : function(channelId, eventId, data) {
		this.initEmployeeDetailsModel(data.context.employee, data.context.campaignId);
	},
	_addItem : function(availablePoints) {
		jQuery.sap.require("sap.m.MessageBox");

		var currentOrder = this.getView().getModel().getProperty("/currentOrder");
		var campPoints = currentOrder.campaign.points;
		var orderPrice = currentOrder.orderPrice;
		var availablePoints = campPoints - orderPrice;
		var dialog = this.byId("addItemCtrl");
		var selItemVal = this.byId("quantityTypeSelect").getSelectedItem().getBindingContext().getObject().value;
		var quantity = this.byId("quantityTypeTxt").getValue();
		var value = selItemVal * quantity;
		var addServiceURL = "api/orders/add/" + this.campaignId;
		var selectUserId = this.employeeProfile.userId;
		if (selectUserId) {
			addServiceURL += "/" + selectUserId;
		}

		if (value <= 0) {
			var alertMsg = sap.ui.getCore().getModel("b_i18n").getProperty("INCORRECT_QUANTITY_MSG");
			sap.m.MessageBox.alert(alertMsg , function() {
			});
		} else if (value <= availablePoints) {
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
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify({
					campaignId : this.campaignId,
					benefitTypeId : this.byId("quantityTypeSelect").getSelectedItem().getKey(),
					quantity : this.byId("quantityTypeTxt").getValue()
				}),
				error : function(xhr, error) {
					sap.m.MessageToast.show(xhr.responseText);
				}
			});
		} else {
			dialog.close();
			sap.m.MessageBox.alert(sap.ui.getCore().getModel("b_i18n").getProperty("LIMIT_EXCEEDED_MSG"), function() {
			});
		}
	},
	_deleteOrder : function(orderId) {
		appController.setAppBusy(true);
		jQuery.ajax({
			url : 'api/orders/' + orderId,
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
	
	_setControlBindCtx : function(control, ctx){
		control.setBindingContext(ctx);
		control.setModel(ctx.getModel());
	}
});
