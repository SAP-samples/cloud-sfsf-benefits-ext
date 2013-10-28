jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Master", {
    onInit: function() {
    },
    onBeforeRendering: function() {
        this.loadModel();
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
        sap.ui.getCore().getEventBus().publish("nav", "to", {
            id: views.BENEFITS_DETAILS_VIEW_ID,
            context: bindingContext
        });
    },
    onNavPressed: function() {
        sap.ui.getCore().getEventBus().publish("nav", "home");
    },
    handleSearch: function() {
        var benefitsList = this.getView().byId("benefitsList");
        var searchField = this.getView().byId("searchField");
        var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
        searchFilter.applySearch(benefitsList, searchField, "name", views.DEFAULT_DETAILS_VIEW_ID);
    }
});