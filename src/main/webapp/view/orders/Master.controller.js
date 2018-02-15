jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.util.AjaxUtil");

sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Master", {
	onInit : function() {
		var oModel = new sap.ui.model.json.JSONModel();
		this.getView().setModel(oModel, "userCamp");
	},
	onBeforeRendering : function() {
		this._loadUserCampaigns();
	},

	_loadUserCampaigns : function() {
		var doneCallback = function(data) {
			this.getView().getModel("userCamp").setData(data);
			this._selectItem();
		};

		var failCallback = function() {
			sap.m.MessageBox.show("{b_i18n>USER_CAMPAIGNS_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR,
					"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		};

		var alwaysCallback = function() {
			this.getView().setBusy(false);
		};

		this.getView().setBusy(true);
		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, "OData.svc/userCampaigns", doneCallback,
				failCallback, alwaysCallback);

	},

	_selectItem : function() {
		this.byId("campaignsList").getBinding("items").sort(new sap.ui.model.Sorter("Active", true));

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
		var event = {};
		jQuery.extend(event, evt);
		var doneCallback = function(data, textStatus, jqXHR) {
			var employee = data.d;
			var activeCampaign = event.getParameter("listItem").getBindingContext("userCamp").getObject();
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
		};

		var failCallback = function() {
			sap.m.MessageBox.show("{b_i18n>USER_PROFILE_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR,
					"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		};

		var alwaysCallback = function() {
			this.getView().setBusy(false);
		};

		this.getView().setBusy(true);
		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, "OData.svc/profile", doneCallback,
				failCallback, alwaysCallback);
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
