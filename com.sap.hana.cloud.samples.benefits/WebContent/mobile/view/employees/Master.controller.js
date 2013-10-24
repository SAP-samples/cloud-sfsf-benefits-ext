sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.employees.Master", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadModel();
            }
        }, this.getView());
        
        this.eventBus = sap.ui.getCore().getEventBus();
    },
    onAfterRendering: function() {
        var list = this.byId("employeesList");
        appController.selectListItem(list, 0);
    },
    onItemSelected: function(evt) {
        var employee = evt.getParameter('listItem').getBindingContext().getObject();
        
        this.eventBus.publish("nav", "to", { 
                id : "EmployeeOrdersDetails",
                additionalData : {modelData : {
                        employee : employee,
                        campaignId : employee.activeCampaignBalance.campaignId
                }}
        });
    },
    onNavPressed: function() {
       this.eventBus.publish("nav", "home");
    },
    handleSearch: function() {
        var employeesList = this.getView().byId("employeesList");
        var searchField = this.getView().byId("searchField");
        appController.search(employeesList, searchField, "fullName");
    },
    loadModel: function() {
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/user/managed", null, false);
    }
});