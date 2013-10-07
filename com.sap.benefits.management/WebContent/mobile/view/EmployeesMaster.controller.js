sap.ui.controller("com.sap.benefits.management.view.EmployeesMaster", {
    onInit: function() {

    },
    onAfterRendering: function() {

    },
    onItemSelected: function(oEvent) {
        appController.showDetails(oEvent);
    },
    onNavPressed: function() {
        appController.goHome();
    }
});