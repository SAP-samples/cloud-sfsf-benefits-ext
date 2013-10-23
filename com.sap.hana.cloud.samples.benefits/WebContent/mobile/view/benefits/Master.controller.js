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
    },
    handleSearch: function() {
        var employeesList = this.getView().byId("benefitsList");
        var searchField = this.getView().byId("searchField");
        appController.search(employeesList, searchField, "name");
    },
});