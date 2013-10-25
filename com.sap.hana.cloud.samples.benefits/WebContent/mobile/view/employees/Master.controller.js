jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.employees.Master", {
    onInit: function() {
        this.eventBus = sap.ui.getCore().getEventBus();
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadModel();
                this.getController().subscribeForOrderModelChange();
            },
            onAfterHide: function(evt) {
                this.getController().unsubscribeForOrderModelChange();
            }
        }, this.getView());
    },
    onAfterRendering: function() {
        var list = this.byId("employeesList");
        var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
        listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
    },
    onItemSelected: function(evt) {
        var employee = evt.getParameter('listItem').getBindingContext().getObject();
        var campaignId = undefined;
        if(employee.activeCampaignBalance){
            campaignId = employee.activeCampaignBalance.campaignId;
        }
        this.eventBus.publish("nav", "to", {
            id: views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID,
            additionalData: {modelData: {
                    employee: employee,
                    campaignId: campaignId
                }}
        });
    },
    onNavPressed: function() {
        this.eventBus.publish("nav", "home");
    },
    subscribeForOrderModelChange: function() {
        this.eventBus.subscribe("refresh", "orders", this._handleModelChanged, this);
    },
    unsubscribeForOrderModelChange: function() {
        this.eventBus.unsubscribe("refresh", "orders", this._handleModelChanged, this);
    },
    handleSearch: function() {
        var employeesList = this.getView().byId("employeesList");
        var searchField = this.getView().byId("searchField");


        var searchFilter = new com.sap.hana.cloud.samples.benefits.common.SearchFilter();
        searchFilter.applySearch(employeesList, searchField, "fullName", views.DEFAULT_DETAILS_VIEW_ID);
    },
    loadModel: function() {
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/user/managed", null, false);
    },
    _handleModelChanged: function() {
        this.loadModel();
    }
});