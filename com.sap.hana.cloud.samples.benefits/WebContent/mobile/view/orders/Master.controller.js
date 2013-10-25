jQuery.sap.require("com.sap.hana.cloud.samples.benefits.common.ListHelper");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Master", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadModel();
                this.byId("campaignsList").getBinding("items").sort(new sap.ui.model.Sorter("active", true));
            }
        }, this.getView());
        this.eventBus = sap.ui.getCore().getEventBus();
    },
    loadModel: function() {
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/user/campaigns", null, false);
    },
    onAfterRendering: function() {
        var list = this.byId("campaignsList");
        var listHelper = new com.sap.hana.cloud.samples.benefits.common.ListHelper();
        listHelper.selectListItem(list, 0, views.DEFAULT_DETAILS_VIEW_ID);
    },
    onItemSelected: function(evt) {
        var employee = jQuery.sap.syncGetJSON("../api/user/profile").data;
        var campaignId = evt.getParameter("listItem").getBindingContext().getObject().id;

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
    isActiveCampaign: function(isActive) {
        return isActive ? "active" : "inactive";
    }
});