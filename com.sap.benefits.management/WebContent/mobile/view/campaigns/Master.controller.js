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
        this.newCampaignInput = new sap.m.Input({
            type: sap.m.InputType.Text,
            placeholder: 'Enter Name ...'
        });

        this.newCampaignListItem = new sap.m.InputListItem({
            content: this.newCampaignInput
        });

        var list = this.byId("campaignsList");
        list.setMode(sap.m.ListMode.None);
        list.addItem(this.newCampaignListItem);

        this.byId("addButton").setEnabled(false);
        this.byId("saveButton").setVisible(true);
    },
    saveButtonPressed: function(evt) {
        var data = sap.ui.getCore().getModel("campaignModel").getData();
        data.campaigns.push({
            name: this.newCampaignInput.getValue(),
            startDate: '',
            endDate: ''});

        sap.ui.getCore().getModel("campaignModel").setData(data);

        var list = this.byId("campaignsList");
        list.setMode(sap.m.ListMode.SingleSelectMaster);
        list.removeItem(this.newCampaignListItem);
        appController.selectListItem(list, 0);

        this.byId("addButton").setEnabled(true);
        this.byId("saveButton").setVisible(false);
    }
});