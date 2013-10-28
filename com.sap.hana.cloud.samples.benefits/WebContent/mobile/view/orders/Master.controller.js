jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Master", {
    onInit: function() {
    },
    loadModel: function() {
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/user/campaigns", null, false);
    },
    onBeforeRendering: function() {
        this.loadModel();
        this.byId("campaignsList").getBinding("items").sort(new sap.ui.model.Sorter("active", true));
    },
    onAfterRendering: function() {
        var list = this.byId("campaignsList");
        if (!list.getItems()) {
            sap.ui.getCore().getEventBus().publish("nav", "to", {
                id: views.DEFAULT_DETAILS_VIEW_ID,
            });
        } else {
            var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
            listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
        }
    },
            onItemSelected: function(evt) {
        var employee = jQuery.sap.syncGetJSON("../api/user/profile").data;
        var campaignId = evt.getParameter("listItem").getBindingContext().getObject().id;

        sap.ui.getCore().getEventBus().publish("app", "ordersDetailsRefresh", {
            context: {
                employee: employee,
                campaignId: campaignId
            }
        });

        sap.ui.getCore().getEventBus().publish("nav", "to", {
            id: views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID,
        });
    },
    onNavPressed: function() {
        sap.ui.getCore().getEventBus().publish("nav", "home");
    },
    isActiveCampaign: function(isActive) {
        return isActive ? "active" : "inactive";
    }
});
