jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Master", {
	onInit : function() {
	},
	loadModel : function() {
		if (!this.getView().getModel()) {
			this.getView().setModel(new sap.ui.model.json.JSONModel());
		}
		this.getView().getModel().loadData("OData.svc/userCampaigns", null, false);
	},
	onBeforeRendering : function() {
		this.loadModel();
		this.byId("campaignsList").getBinding("items").sort(new sap.ui.model.Sorter("Active", true));
	},
	onAfterRendering : function() {
		var list = this.byId("campaignsList");
		if (!list.getItems()) {
			sap.ui.getCore().getEventBus().publish("nav", "to", {
				id : views.DEFAULT_DETAILS_VIEW_ID
			});
		} else {
			var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
			listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
		}
	},
	setState : function(active) {
		return active ? sap.ui.core.ValueState.Success : sap.ui.core.ValueState.Error;
	},
	onItemSelected : function(evt) {
		var employee = jQuery.sap.syncGetJSON("OData.svc/profile").data.d;
		var activeCampaign = evt.getParameter("listItem").getBindingContext().getObject();
		var campaignId = activeCampaign.Id;
		sap.ui.getCore().getEventBus().publish("app", "ordersDetailsRefresh", {
			context : {
				employee : employee,
				campaignId : campaignId,
				activeCampaign : activeCampaign
			}
		});

		sap.ui.getCore().getEventBus().publish("nav", "to", {
			id : views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID
		});
	},
	onNavPressed : function() {
		sap.ui.getCore().getEventBus().publish("nav", "home");
	},
	isActiveCampaign : function(active) {
		var activeMsg = sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_STATUS_ACTIVE");
		var inactiveMsg = sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_STATUS_INACTIVE");
		return active ? activeMsg : inactiveMsg;
	}
});
