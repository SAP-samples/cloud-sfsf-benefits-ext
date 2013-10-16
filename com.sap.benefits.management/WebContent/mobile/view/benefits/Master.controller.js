sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Master", {
    onInit: function() {

    },
    onAfterRendering: function() {
        var list = this.byId("benefitsList");
        appController.selectListItem(list, 0);
    },
    onItemSelected: function(oEvent) {
        appController.benefitItemSelected(oEvent);
    },
    onNavPressed: function() {
        appController.goHome();
    }
});