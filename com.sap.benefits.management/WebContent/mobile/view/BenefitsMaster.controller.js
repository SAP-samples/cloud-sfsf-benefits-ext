sap.ui.controller("com.sap.benefits.management.view.BenefitsMaster", {
    onInit: function() {

    },
    onAfterRendering: function() {

    },
    onItemSelected: function(oEvent) {
        appController.benefitItemSelected(oEvent);
    },
    onNavPressed: function() {
        appController.goHome();
    }
});