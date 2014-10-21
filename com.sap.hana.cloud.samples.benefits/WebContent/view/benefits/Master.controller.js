jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Master", {
	onInit : function() {
		this.initModel();
	},

	initModel : function() {
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.attachRequestFailed(function() {
			sap.m.MessageBox.show("{b_i18n>BENEFITS_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR, "{b_i18n>ERROR_TITLE}",
					[sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		});
		this.getView().setModel(oModel);
	},

	onBeforeRendering : function() {
		this.loadModel();
	},

	loadModel : function() {
		var oModel = this.getView().getModel();
		if (Object.keys(oModel.getData()).length === 0) {
			oModel.loadData("OData.svc/BenefitInfos?$expand=BenefitTypeDetails", null, false);
		}
	},
	onAfterRendering : function() {
		var list = this.byId("benefitsList");
		var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
		listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
		this.getView().byId("searchField").focus();
	},
	onItemSelected : function(evt) {
		var bindingContext = evt.getParameter('listItem').getBindingContext();

		sap.ui.getCore().getEventBus().publish("app", "benefitsDetailsRefresh", {
			id : views.CAMPAIGN_DETAILS_VIEW_ID,
			context : bindingContext
		});

		sap.ui.getCore().getEventBus().publish("nav", "to", {
			id : views.BENEFITS_DETAILS_VIEW_ID
		});
	},
	onNavPressed : function() {
		sap.ui.getCore().getEventBus().publish("nav", "home");
	},
	handleSearch : function() {
		var benefitsList = this.getView().byId("benefitsList");
		var searchField = this.getView().byId("searchField");
		var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
		searchFilter.applySearch(benefitsList, searchField, "Name", views.DEFAULT_DETAILS_VIEW_ID);
	}
});
