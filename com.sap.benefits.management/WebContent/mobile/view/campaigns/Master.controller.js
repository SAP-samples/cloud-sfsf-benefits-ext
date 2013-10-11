sap.ui.controller("com.sap.benefits.management.view.campaigns.Master", {
    onInit: function() {
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
            press: jQuery.proxy(function() {
                var newCampaignName = this.byId("nameCtr").getValue();
                jQuery.ajax({
                    url: '/com.sap.benefits.management/api/campaigns/admin',
                    type: 'post',
                    dataType: 'json',
                    success: jQuery.proxy(function(data) {
                        appController.reloadCampaignModel();
                        var list = this.byId("campaignsList");
                        var newItemIndex = list.getItems().length - 1;
                        appController.selectListItem(list, newItemIndex);
                    }, this),
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({name: newCampaignName, startDate: null, endDate: null})
                });
                newCampDialog.close();
            }, this)
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
});