jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Master", {
	onInit : function() {
		this.listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
		// subscribe to event bus
		sap.ui.getCore().getEventBus().subscribe("refresh", "campaigns", this._handleModelChanged, this);
		this.initModel();
	},

	initModel : function() {
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.attachRequestFailed(function() {
			sap.m.MessageBox.show("{b_i18n>HR_CAMPAIGNS_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR,
					"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		});
		this.getView().setModel(oModel);
	},

	onBeforeRendering : function() {
		this.loadModel(false);
		this.checkToShowDefaultPage();
		this.byId("campaignsList").getBinding("items").sort(new sap.ui.model.Sorter("Name", false));
	},
	loadModel : function(hasNewChanges) {
		var oModel = this.getView().getModel();
		if (Object.keys(oModel.getData()).length === 0 || hasNewChanges) {
			oModel.loadData("OData.svc/hrCampaigns", null, false);
		}
	},
	onAfterRendering : function() {
		var list = this.byId("campaignsList");
		this.listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
		this.getView().byId("searchField").focus();
	},
	checkToShowDefaultPage : function() {
		var list = this.byId("campaignsList");
		if (!list.getItems()) {
			sap.ui.getCore().getEventBus().publish("nav", "to", {
				id : views.DEFAULT_DETAILS_VIEW_ID
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
			context : bindingContext
		});
	},
	handleSearch : function() {
		var campaignsList = this.getView().byId("campaignsList");
		var searchField = this.getView().byId("searchField");
		var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
		searchFilter.applySearch(campaignsList, searchField, "Name", views.DEFAULT_DETAILS_VIEW_ID);
	},
	resetDialog : function() {
		sap.ui.getCore().byId("newCampDialog--nameCtr").setValue(null);
		sap.ui.getCore().byId("newCampDialog--nameCtr").setValueState(sap.ui.core.ValueState.None);
		this._changeOkButtonState(true);

	},
	addButtonPressed : function(evt) {
		if (!this.newCampDialog) {
			this.newCampDialog = sap.ui.xmlfragment("newCampDialog", "view.campaigns.addCampaignDialog", this);
		}
		sap.ui.getCore().getEventBus().publish("nav", "virtual");
		this.newCampDialog.open();

	},
	setFocus : function(evt) {
		sap.ui.getCore().byId("newCampDialog--nameCtr").focus();
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
		this.validateNewCampaignName();
		var isValidName = sap.ui.getCore().byId("newCampDialog--nameCtr").getValueState();
		if (isValidName === sap.ui.core.ValueState.None) {
			this.newCampDialog.close();
			var newCampaignName = sap.ui.getCore().byId("newCampDialog--nameCtr").getValue();
			appController.setAppBusy(true);
			jQuery.ajax({
				url : 'OData.svc/addCampaign?name=\'' + newCampaignName + '\'',
				type : 'post',
				dataType : 'json',
				success : jQuery.proxy(function(data) {
					this.loadModel(true);
					this._selectCampaignByName(newCampaignName);
				}, this),
				contentType : "application/json; charset=utf-8",
				complete : function() {
					appController.setAppBusy(false);
				}
			}).fail(function(oResponseData) {
				sap.m.MessageBox.alert(sap.ui.getCore().getModel("b_i18n").getProperty("POST_NEW_CAMPAIGN_FAILED"));
			});
		}
	},
	cancelButtonPressed : function(evt) {
		this.newCampDialog.close();
	},
	_navigateToDetailsPage : function(bindingContext) {
		sap.ui.getCore().getEventBus().publish("nav", "to", {
			id : views.CAMPAIGN_DETAILS_VIEW_ID,
			context : bindingContext
		});
	},
	_selectCampaignByName : function(name) {
		var list = this.byId("campaignsList");
		var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
		var items = list.getItems();
		for (var i = 0; i < items.length; i++) {
			if (items[i].getBindingContext().getObject().Name === name) {
				listHelper.selectListItem(list, i, views.DEFAULT_DETAILS_VIEW_ID);
				return;
			}
		}

		listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
	},
	_handleModelChanged : function(channelId, eventId, data) {
		var list = this.byId("campaignsList");
		var selectedItemId = list.getSelectedItem().getBindingContext().getObject().Id;
		if (selectedItemId) {
			this.loadModel(true);
			var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
			var items = list.getItems();
			for (var i = 0; i < items.length; i++) {
				if (items[i].getBindingContext().getObject().Id === selectedItemId) {
					listHelper.selectListItem(list, i, views.DEFAULT_DETAILS_VIEW_ID);
					return;
				}
			}

			listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
		} else {
			this.loadModel(true);
		}
	},
	validateNewCampaignName : function() {
		var nameCtr = sap.ui.getCore().byId("newCampDialog--nameCtr");
		var name = nameCtr.getValue();
		if (name.length > 0) {
			if (this._isNewCampaignNameAlreadyExists(name)) {
				nameCtr.setValueStateText(sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_EXIST_MSG"));
				nameCtr.setValueState(sap.ui.core.ValueState.Error);
			} else {
				nameCtr.setValueState(sap.ui.core.ValueState.None);
			}
		} else {
			nameCtr.setValueStateText(sap.ui.getCore().getModel("b_i18n").getProperty("INVALID_CAMPAIGN_NAME_MSG"));
			nameCtr.setValueState(sap.ui.core.ValueState.Error);
			this._changeOkButtonState(false);
		}
	},

	_isNewCampaignNameAlreadyExists : function(sNewCampaignName) {
		var oModelData = this.getView().getModel().getData();
		var aCampaigns = oModelData.d && oModelData.d.results;

		if (!aCampaigns) {
			sap.m.MessageBox.show("{b_i18n>MISSING_CAMPAIGNS_DATA}", sap.m.MessageBox.Icon.ERROR, "{b_i18n>ERROR_TITLE}",
					[sap.m.MessageBox.Action.OK]);
			return true;
		}

		return aCampaigns.some(function(campaign) {
			return campaign.Name === sNewCampaignName;
		});
	},

	_changeOkButtonState : function(enabled) {
		var isValidName = sap.ui.getCore().byId("newCampDialog--nameCtr").getValueState();
		var buttonEnabled = enabled && (isValidName === sap.ui.core.ValueState.None);

		sap.ui.getCore().byId("newCampDialog--okButtonId").setEnabled(buttonEnabled);
	}

});