sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Master", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadModel();
            }
        }, this.getView());

        this.eventBus = sap.ui.getCore().getEventBus();
    },
    loadModel: function() {
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/benefits/all", null, false);
    },
    onAfterRendering: function() {
        var list = this.byId("benefitsList");
        appController.selectListItem(list, 0);
    },
    onItemSelected: function(evt) {
        var bindingContext = evt.getParameter('listItem').getBindingContext();
        this.eventBus.publish("nav", "to", {
            id: "BenefitsDetails",
            context: bindingContext
        });
    },
    onNavPressed: function() {
        this.eventBus.publish("nav", "home");
    },
    handleSearch: function() {
        var employeesList = this.getView().byId("benefitsList");
        var searchField = this.getView().byId("searchField");
        appController.search(employeesList, searchField, "name");
    },
});