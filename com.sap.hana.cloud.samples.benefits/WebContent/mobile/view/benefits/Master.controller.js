jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
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
        var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
        listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
    },
    onItemSelected: function(evt) {
        var bindingContext = evt.getParameter('listItem').getBindingContext();
        this.eventBus.publish("nav", "to", {
            id: views.BENEFITS_DETAILS_VIEW_ID,
            context: bindingContext
        });
    },
    onNavPressed: function() {
        this.eventBus.publish("nav", "home");
    },
    handleSearch: function() {
        var employeesList = this.getView().byId("benefitsList");
        var searchField = this.getView().byId("searchField");
        var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
        searchFilter.applySearch(employeesList, searchField, "name", views.DEFAULT_DETAILS_VIEW_ID);
    },
});