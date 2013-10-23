sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Master", {
    onInit: function() {
    },
    onAfterRendering: function() {
        var list = this.byId("campaignsList");
        appController.selectListItem(list, 0);
    },
    onItemSelected: function(oEvent) {
        appController.handleUserCampaignItemSelect(oEvent);
    },
    onNavPressed: function() {
        appController.goHome();
    },
	isActiveCampaign : function(isActive){
		if(isActive) {
			return "active";
		} else {
			return "inactive";
		}
	}
});