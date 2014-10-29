jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.util.AjaxUtil");

sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Master", {
	onInit : function() {
		this.getView().setModel(new sap.ui.model.json.JSONModel());
	},

	onBeforeRendering : function() {
		if (!jQuery.isEmptyObject(this.getView().getModel().getData())) {
			var list = this.byId("benefitsList");
			var selectedItemIndex = list.indexOfItem(list.getSelectedItem());
			this._navigateToItem(selectedItemIndex >= 0 ? selectedItemIndex : 0);
			return;
		}

		var benefitInfoDeferred = this._loadBenefitInfoModel();
		benefitInfoDeferred.done(this._navigateToFirstItem);
	},

	_loadBenefitInfoModel : function() {
		var doneCallback = function(data) {
			this.getView().getModel().setData(data);
		};

		var failCallback = function() {
			sap.m.MessageBox.show("{b_i18n>BENEFITS_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR, "{b_i18n>ERROR_TITLE}",
					[sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		};

		var alwaysCallback = function() {
			this.getView().setBusy(false);
		};

		this.getView().setBusy(true);
		return com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this,
				"OData.svc/BenefitInfos?$expand=BenefitTypeDetails", doneCallback, failCallback, alwaysCallback);
	},

	_navigateToFirstItem : function() {
		this._navigateToItem(0);
	},

	_navigateToItem : function(index) {
		var list = this.byId("benefitsList");
		var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
		listHelper.selectListItem(list, index, views.DEFAULT_DETAILS_VIEW_ID);
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
