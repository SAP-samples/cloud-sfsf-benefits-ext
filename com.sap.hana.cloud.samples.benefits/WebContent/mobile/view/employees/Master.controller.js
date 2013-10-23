sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.employees.Master", {
    onInit: function() {

    },
    onAfterRendering: function() {
        var list = this.byId("employeesList");
        appController.selectListItem(list, 0);
    },
    onItemSelected: function(oEvent) {
        appController.employeeItemSelected(oEvent);
    },
    onNavPressed: function() {
        appController.goHome();
    },
    handleSearch: function() {
        appController.openDefaultDetailsPage();
        var employeesList = this.getView().byId("employeesList");
        var searchField = this.getView().byId("searchField");
        var showSearch = (searchField.getValue().length !== 0);
        var binding = employeesList.getBinding("items");

        if (binding) {
            if(showSearch){
            var filterName = new sap.ui.model.Filter("fullName", sap.ui.model.FilterOperator.Contains, searchField.getValue());
            binding.filter([filterName]);
            }else {
                binding.filter([]);
            }
        }
    },
});