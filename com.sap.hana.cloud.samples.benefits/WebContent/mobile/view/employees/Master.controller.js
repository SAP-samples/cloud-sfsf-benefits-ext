jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.employees.Master", {
    onInit: function() {
        sap.ui.getCore().getEventBus().subscribe("refresh", "orders", this._handleModelChanged, this);
    },
    onBeforeRendering: function() {
        this.loadModel();
    },
    onAfterRendering: function() {
        var list = this.byId("employeesList");
        var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
        listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);

    },
    onItemSelected: function(evt) {
        var employee = evt.getParameter('listItem').getBindingContext().getObject();
        var campaignId = undefined;
        if (employee.activeCampaignBalance) {
            campaignId = employee.activeCampaignBalance.campaignId;

            sap.ui.getCore().getEventBus().publish("app", "ordersDetailsRefresh", {
                context: {
                    employee: employee,
                    campaignId: campaignId
                }
            });

            sap.ui.getCore().getEventBus().publish("nav", "to", {
                id: views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID
            });
        } else {
            sap.ui.getCore().getEventBus().publish("nav", "to", {
                id: views.DEFAULT_DETAILS_VIEW_ID
            });
        }
    },
    onNavPressed: function() {
        sap.ui.getCore().getEventBus().publish("nav", "home");
    },
    checkToShowDefaultPage: function() {
        var data = this.getView().getModel().getData();
        if (data[0] && !data[0].activeCampaignBalance) {
            sap.ui.getCore().getEventBus().publish("nav", "to", {
                id: views.DEFAULT_DETAILS_VIEW_ID
            });
        } else if (data[0].activeCampaignBalance) {
            sap.ui.getCore().getEventBus().publish("nav", "to", {
                id: views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID
            });
        }
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