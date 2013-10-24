jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Master", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadModel();
            }
        }, this.getView());

        this.dialogOkBtn = new sap.m.Button({
            text: "Ok",
            press: jQuery.proxy(this.okButtonPressed, this)
        });

        this.dialogCancelBtn = new sap.m.Button({
            text: "Cancel",
            press: jQuery.proxy(function() {
                this.byId("newcampaignDialog").close();
            }, this)
        });

        this.byId("nameCtr").attachChange(jQuery.proxy(this._validateNewCampaignName, this), this);
        this.byId("pointsCtr").attachLiveChange(jQuery.proxy(this._validateNewCampaignPoints, this), this);

        // subscribe to event bus
        this.eventBus = sap.ui.getCore().getEventBus();
        this.eventBus.subscribe("refresh", "campaigns", this._handleModelChanged, this);
    },
    loadModel: function() {
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/campaigns/", null, false);
    },
    onAfterRendering: function() {
        this.selectFirstCampaign();
    },
    onNavPressed: function() {
        this.eventBus.publish("nav", "home");
    },
    onItemSelect: function(evt) {
        var bindingContext = evt.getParameter('listItem').getBindingContext();
        this.eventBus.publish("nav", "to", {
            id: views.CAMPAIGN_DETAILS_VIEW_ID,
            context: bindingContext,
        });
    },
    selectFirstCampaign: function() {
        var list = this.byId("campaignsList");
        var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
        listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
    },
    handleSearch: function() {
        var employeesList = this.getView().byId("campaignsList");
        var searchField = this.getView().byId("searchField");
        var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
        searchFilter.applySearch(employeesList, searchField, "name", views.DEFAULT_DETAILS_VIEW_ID);
    },
    addButtonPressed: function(evt) {
        var newCampDialog = this.byId("newcampaignDialog");
        this.byId("nameCtr").setValue(null);
        this.byId("pointsCtr").setValue(null);
        this.byId("nameCtr").setValueState(sap.ui.core.ValueState.None);
        this.byId("pointsCtr").setValueState(sap.ui.core.ValueState.None);
        this._changeOkButtonState(true);
        newCampDialog.setLeftButton(this.dialogOkBtn);
        newCampDialog.setRightButton(this.dialogCancelBtn);

        sap.ui.getCore().getEventBus().publish("nav", "virtual");
        newCampDialog.open();

    },
    setState: function(active) {
        return active ? sap.ui.core.ValueState.Success : sap.ui.core.ValueState.Error;
    },
    setStateText: function(active) {
        return active ? "Active" : "Inactive";
    },
    okButtonPressed: function(evt) {
        this._validateNewCampaignName();
        this._validateNewCampaignPoints();
        var isValidName = this.byId("nameCtr").getValueState();
        var isValidPoints = this.byId("pointsCtr").getValueState();
        if ((isValidName === sap.ui.core.ValueState.None) && (isValidPoints === sap.ui.core.ValueState.None)) {
            this.byId("newcampaignDialog").close();
            var newCampaignName = this.byId("nameCtr").getValue();
            var newCampaignPoints = this.byId("pointsCtr").getValue();
            appController.setAppBusy(true);
            jQuery.ajax({
                url: '../api/campaigns/',
                type: 'post',
                dataType: 'json',
                success: jQuery.proxy(function(data) {
                    this.loadModel();
                    var list = this.byId("campaignsList");
                    var newItemIndex = list.getItems().length - 1;
                    var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
                    listHelper.selectListItem(list, newItemIndex, views.DEFAULT_DETAILS_VIEW_ID);
                }, this),
                complete: function() {
                    appController.setAppBusy(false);
                },
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({name: newCampaignName, startDate: null, endDate: null, points: newCampaignPoints})
            });
        }
    },
    _handleModelChanged: function(channelId, eventId, data) {
        var list = this.byId("campaignsList");
        var selectedItem = list.getSelectedItem();
        if (selectedItem) {
            var index = data.action === 'delete' ? 0 : list.indexOfItem(selectedItem);
            this.loadModel();
            appController.selectListItem(list, index);
            var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
            listHelper.selectListItem(list, index, views.DEFAULT_DETAILS_VIEW_ID);
        } else {
            this.loadModel();
        }
    },
    _validateNewCampaignName: function() {
        var newCampDialog = this.byId("newcampaignDialog");
        var nameCtr = this.byId("nameCtr");
        var name = nameCtr.getValue();
        if (name.length > 0) {
            newCampDialog.setBusy(true);
            jQuery.ajax({
                url: '../api/campaigns/check-name-availability?name=' + jQuery.sap.encodeURL(name),
                type: 'get',
                dataType: 'json',
                success: jQuery.proxy(function(data) {
                    if (!data.isAvailable) {
                        nameCtr.setValueStateText("Already have a campaign with that name");
                        nameCtr.setValueState(sap.ui.core.ValueState.Error);
                    } else {
                        nameCtr.setValueState(sap.ui.core.ValueState.None);
                    }
                }, this),
                complete: function() {
                    newCampDialog.setBusy(false);
                },
                contentType: "application/json; charset=utf-8",
            });
        } else {
            nameCtr.setValueStateText("Valid name is required");
            nameCtr.setValueState(sap.ui.core.ValueState.Error);
            this._changeOkButtonState(false);
        }
    },
    _validateNewCampaignPoints: function() {
        var pointsCtr = this.byId("pointsCtr");
        var points = pointsCtr.getValue();
        if (jQuery.isNumeric(points) && points > 0) {
            pointsCtr.setValueState(sap.ui.core.ValueState.None);
        } else if (points.length === 0) {
            pointsCtr.setValueStateText("Valid Points is required");
            pointsCtr.setValueState(sap.ui.core.ValueState.Error);
        } else {
            pointsCtr.setValueStateText("Not a valid positive number");
            pointsCtr.setValueState(sap.ui.core.ValueState.Error);
        }
    },
    _changeOkButtonState: function(enabled) {
        var isValidName = this.byId("nameCtr").getValueState();
        var isValidPoints = this.byId("pointsCtr").getValueState();
        if (enabled && (isValidName === sap.ui.core.ValueState.None) && (isValidPoints === sap.ui.core.ValueState.None)) {
            this.dialogOkBtn.setEnabled(true);
        } else {
            this.dialogOkBtn.setEnabled(false);
        }
    }
});