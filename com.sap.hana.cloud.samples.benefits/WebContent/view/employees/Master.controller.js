jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.employees.Master", {
	onInit : function() {
		this._initModel();
		sap.ui.getCore().getEventBus().subscribe("refresh", "orders", this.loadModel, this);

	},

	_initModel : function() {
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.attachRequestFailed(function() {
			sap.m.MessageBox.show("{b_i18n>MANAGED_EMPLOYEES_DATA_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR,
					"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		});
		this.getView().setModel(oModel);
	},
	onBeforeRendering : function() {
		this.loadModel();
	},
	onAfterRendering : function() {
		var list = this.byId("employeesList");
		var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
		listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
		this.getView().byId("searchField").focus();
		$("[id$=-trigger]").attr('tabindex', 0);
		$("[id$=-trigger]").addClass("itemFocus");
	},
	onItemSelected : function(evt) {
		var employee = evt.getParameter('listItem').getBindingContext().getObject();
		var employeePointsDetails = employee.UserPointsDetails.results;
		var campaignId = undefined;
		var activeCampaign = this.getActiveCampaign(employeePointsDetails);

		if (activeCampaign) {
			campaignId = activeCampaign.Id;

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
		} else {
			sap.ui.getCore().getEventBus().publish("nav", "to", {
				id : views.DEFAULT_DETAILS_VIEW_ID
			});
		}
	},
	onNavPressed : function() {
		sap.ui.getCore().getEventBus().publish("nav", "home");
	},
	handleSearch : function() {
		var employeesList = this.getView().byId("employeesList");
		var employeeArray = employeesList.getBinding("items").oList;
		employeeArray.forEach(function(employee) {
			employee.FullName = employee.FirstName + " " + employee.LastName;
		});
		var searchField = this.getView().byId("searchField");
		var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
		searchFilter.applySearch(employeesList, searchField, "FullName", views.DEFAULT_DETAILS_VIEW_ID);
	},
	loadModel : function() {
		this.getView().getModel().loadData("OData.svc/managed?$expand=UserPointsDetails/CampaignDetails", null, false);
		this.handleSearch();
	},
	getAvailablePoints : function(userPointsDetails) {
		var i = 0;
		for (i; i < userPointsDetails.length; i++) {
			if (userPointsDetails[i].CampaignDetails.Active) {
				return userPointsDetails[i].AvailablePoints;
			}
		}
		return null;
	},
	getActiveCampaign : function(userPointsDetails) {
		var i = 0;
		for (i; i < userPointsDetails.length; i++) {
			if (userPointsDetails[i].CampaignDetails.Active) {
				return userPointsDetails[i].CampaignDetails;
			}
		}
		return false;

	}
});
