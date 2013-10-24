sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Master", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadModel();
            }
        }, this.getView());
        this.eventBus = sap.ui.getCore().getEventBus();
    },
    loadModel: function(){
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().loadData("../api/user/campaigns", null, false);
    },
    onAfterRendering: function() {
        var list = this.byId("campaignsList");
        appController.selectListItem(list, 0);
    },
    onItemSelected: function(evt) {
        var employee = jQuery.sap.syncGetJSON("../api/user/profile").data;
        var campaignId = evt.getParameter("listItem").getBindingContext().getObject().id;

        this.eventBus.publish("nav", "to", {
            id: "EmployeeOrdersDetails",
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
        if (isActive) {
            return "active";
        } else {
            return "inactive";
        }
    }
});