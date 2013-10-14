sap.ui.controller("com.sap.benefits.management.view.campaigns.Master", {
    onInit: function() {
        this.busyDialog = new sap.m.BusyDialog({showCancelButton: false});
    },
    onAfterRendering: function() {
        var list = this.byId("campaignsList");
        appController.selectListItem(list, 0);
    },
    onNavPressed: function() {
        appController.goHome();
    },
    onItemSelect: function(evt) {
        appController.campaignItemSelected(evt);
    },
    addButtonPressed: function(evt) {
        var newCampDialog = this.byId("newcampaignDialog");
        this.byId("nameCtr").setValue(null);
        newCampDialog.setLeftButton(new sap.m.Button({
            text: "Ok",
            press: jQuery.proxy(this.okButtonPressed, this)
        }));
        newCampDialog.setRightButton(new sap.m.Button({
            text: "Cancel",
            press: function() {
                newCampDialog.close();
            }
        }));

        sap.ui.getCore().getEventBus().publish("nav", "virtual");
        newCampDialog.open();

    },
    setState: function(active) {
        jQuery.sap.require("sap.ui.core.ValueState");
        if (active) {
            return sap.ui.core.ValueState.Success;
        } else {
            return sap.ui.core.ValueState.Error;
        }
    },
    setStateText: function(active) {
        if (active) {
            return "Active";
        } else {
            return "Inactive";
        }
    },
    okButtonPressed: function(evt) {
        this.byId("newcampaignDialog").close();
        var newCampaignName = this.byId("nameCtr").getValue();
        var newCampaignPoints = this.byId("pointsCtr").getValue();
        this.busyDialog.open();
        jQuery.ajax({
            url: '/com.sap.benefits.management/api/campaigns/admin',
            type: 'post',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                appController.reloadCampaignModel();
                this.busyDialog.close();
                var list = this.byId("campaignsList");
                var newItemIndex = list.getItems().length - 1;
                appController.selectListItem(list, newItemIndex);
            }, this),
            error: jQuery.proxy(function() {
                this.busyDialog.close();
            }, this),
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({name: newCampaignName, startDate: null, endDate: null, points: newCampaignPoints})
        });
    }
});