jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Master", {
	onInit : function() {
		this.listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
	// subscribe to event bus
		sap.ui.getCore().getEventBus().subscribe("refresh", "campaigns", this._handleModelChanged, this);
	},
	onBeforeRendering : function() {
		this.loadModel();
		this.checkToShowDefaultPage();
		this.byId("campaignsList").getBinding("items").sort(new sap.ui.model.Sorter("name", false));
	},
	loadModel : function() {
		if (!this.getView().getModel()) {
			this.getView().setModel(new sap.ui.model.json.JSONModel());
		}
		this.getView().getModel().loadData("api/campaigns/", null, false);
	},
	onAfterRendering : function() {
		var list = this.byId("campaignsList");
		this.listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
	},
	checkToShowDefaultPage : function() {
		var list = this.byId("campaignsList");
		if (!list.getItems()) {
			sap.ui.getCore().getEventBus().publish("nav", "to", {
				id : views.DEFAULT_DETAILS_VIEW_ID,
			});
		}
	},
	onNavPressed : function() {
		sap.ui.getCore().getEventBus().publish("nav", "home");
	},
	onItemSelect : function(evt) {
		var bindingContext = evt.getParameter('listItem').getBindingContext();
		this._navigateToDetailsPage(bindingContext);
		sap.ui.getCore().getEventBus().publish("app", "campaignDetailsRefresh", {
			id : views.CAMPAIGN_DETAILS_VIEW_ID,
			context : bindingContext,
		});
	},
	handleSearch : function() {
		var campaignsList = this.getView().byId("campaignsList");
		var searchField = this.getView().byId("searchField");
		var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
		searchFilter.applySearch(campaignsList, searchField, "name", views.DEFAULT_DETAILS_VIEW_ID);
	},
	resetDialog : function(){
		sap.ui.getCore().byId("newCampDialog--nameCtr").setValue(null);
		sap.ui.getCore().byId("newCampDialog--pointsCtr").setValue(null);
		sap.ui.getCore().byId("newCampDialog--nameCtr").setValueState(sap.ui.core.ValueState.None);
		sap.ui.getCore().byId("newCampDialog--pointsCtr").setValueState(sap.ui.core.ValueState.None);
		this._changeOkButtonState(true);
	},
	addButtonPressed : function(evt) {
		if (!this.newCampDialog) {
			this.newCampDialog = sap.ui.xmlfragment("newCampDialog", "view.campaigns.addCampaignDialog", this);
		}
		sap.ui.getCore().byId("newCampDialog--nameCtr").attachChange(jQuery.proxy(this._validateNewCampaignName, this), this);
		sap.ui.getCore().byId("newCampDialog--pointsCtr").attachLiveChange(jQuery.proxy(this._validateNewCampaignPoints, this), this);
		sap.ui.getCore().getEventBus().publish("nav", "virtual");
		this.newCampDialog.open();

	},	
	setState : function(active) {
		return active ? sap.ui.core.ValueState.Success : sap.ui.core.ValueState.Error;
	},
	setStateText : function(active) {
		var activeMsg = sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_STATUS_ACTIVE");
		var inactiveMsg = sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_STATUS_INACTIVE");
		return active ? activeMsg : inactiveMsg;
	},
	okButtonPressed : function(evt) {
		this._validateNewCampaignName();
		this._validateNewCampaignPoints();
		var isValidName = sap.ui.getCore().byId("newCampDialog--nameCtr").getValueState();
		var isValidPoints = sap.ui.getCore().byId("newCampDialog--pointsCtr").getValueState();
		if ((isValidName === sap.ui.core.ValueState.None) && (isValidPoints === sap.ui.core.ValueState.None)) {
			this.newCampDialog.close();
			var newCampaignName = sap.ui.getCore().byId("newCampDialog--nameCtr").getValue();
			var newCampaignPoints = sap.ui.getCore().byId("newCampDialog--pointsCtr").getValue();
			appController.setAppBusy(true);
			jQuery.ajax({
				url : 'api/campaigns/',
				type : 'post',
				dataType : 'json',
				success : jQuery.proxy(function(data) {
					this.loadModel();
					this._selectCampaignByName(newCampaignName);
				}, this),
				complete : function() {
					appController.setAppBusy(false);
				},
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify({
					name : newCampaignName,
					startDate : null,
					endDate : null,
					points : newCampaignPoints
				})
			});
		}
	},
	cancelButtonPressed : function(evt){
		this.newCampDialog.close();
	},
	_navigateToDetailsPage : function(bindingContext) {
		sap.ui.getCore().getEventBus().publish("nav", "to", {
			id : views.CAMPAIGN_DETAILS_VIEW_ID,
			context : bindingContext,
		});
	},
	_selectCampaignByName : function(name) {
		var list = this.byId("campaignsList");
		var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
		var items = list.getItems();
		for ( var i = 0; i < items.length; i++) {
			if (items[i].getBindingContext().getObject().name === name) {
				listHelper.selectListItem(list, i, views.DEFAULT_DETAILS_VIEW_ID);
				return;
			}
		}

		listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
	},
	_handleModelChanged : function(channelId, eventId, data) {
		var list = this.byId("campaignsList");
		var selectedItemId = list.getSelectedItem().getBindingContext().getObject().id;
		if (selectedItemId) {
			this.loadModel();
			var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
			var items = list.getItems();
			for ( var i = 0; i < items.length; i++) {
				if (items[i].getBindingContext().getObject().id === selectedItemId) {
					listHelper.selectListItem(list, i, views.DEFAULT_DETAILS_VIEW_ID);
					return;
				}
			}

			listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
		} else {
			this.loadModel();
		}
	},
	_validateNewCampaignName : function() {
		var nameCtr = sap.ui.getCore().byId("newCampDialog--nameCtr");
		var name = nameCtr.getValue();
		if (name.length > 0) {
			this.newCampDialog.setBusy(true);
			jQuery.ajax({
				url : 'api/campaigns/check-name-availability?name=' + jQuery.sap.encodeURL(name),
				type : 'get',
				dataType : 'json',
				success : jQuery.proxy(function(data) {
					if (!data.isAvailable) {
						nameCtr.setValueStateText(sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_EXIST_MSG"));
						nameCtr.setValueState(sap.ui.core.ValueState.Error);
					} else {
						nameCtr.setValueState(sap.ui.core.ValueState.None);
					}
				}, this),
				complete : jQuery.proxy(function() {
					this.newCampDialog.setBusy(false);
				}, this),
				contentType : "application/json; charset=utf-8",
			});
		} else {
			nameCtr.setValueStateText(sap.ui.getCore().getModel("b_i18n").getProperty("INVALID_CAMPAIGN_NAME_MSG"));
			nameCtr.setValueState(sap.ui.core.ValueState.Error);
			this._changeOkButtonState(false);
		}
	},
	_validateNewCampaignPoints : function() {
		var pointsCtr = sap.ui.getCore().byId("newCampDialog--pointsCtr");
		var points = pointsCtr.getValue();
		if (jQuery.isNumeric(points) && points > 0) {
			pointsCtr.setValueState(sap.ui.core.ValueState.None);
		} else if (points.length === 0) {
			pointsCtr.setValueStateText(sap.ui.getCore().getModel("b_i18n").getProperty("INVALID_CAMPAIGN_POINTS_MSG"));
			pointsCtr.setValueState(sap.ui.core.ValueState.Error);
		} else {
			pointsCtr.setValueStateText(sap.ui.getCore().getModel("b_i18n").getProperty("NOT_POSITIVE_NUMBER_MSG"));
			pointsCtr.setValueState(sap.ui.core.ValueState.Error);
		}
	},
	_changeOkButtonState : function(enabled) {
		var isValidName = sap.ui.getCore().byId("newCampDialog--nameCtr").getValueState();
		var isValidPoints = sap.ui.getCore().byId("newCampDialog--pointsCtr").getValueState();
		if (enabled && (isValidName === sap.ui.core.ValueState.None) && (isValidPoints === sap.ui.core.ValueState.None)) {
			sap.ui.getCore().byId("newCampDialog--okButtonId").setEnabled(true);
		} else {
			sap.ui.getCore().byId("newCampDialog--okButtonId").setEnabled(false);
		}
	}
	
});