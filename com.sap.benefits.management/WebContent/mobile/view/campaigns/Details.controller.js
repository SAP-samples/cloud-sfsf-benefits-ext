sap.ui.controller("com.sap.benefits.management.view.campaigns.Details", {
    onInit: function() {

    },
    onAfterRendering: function() {
    },
    saveButtonPressed: function(evt) {
        jQuery.sap.require("sap.m.MessageToast");
        
        var bindingPath = sap.ui.getCore().getModel("campaignDetailsModel").getData().bindingPath;
        sap.ui.getCore().getModel("campaignModel").setProperty(bindingPath + "/name", this.byId("nameCtr").getValue());
        sap.ui.getCore().getModel("campaignModel").setProperty(bindingPath + "/startDate", this.byId("startDateCtr").getValue());
        sap.ui.getCore().getModel("campaignModel").setProperty(bindingPath + "/endDate", this.byId("endDateCtr").getValue());
        
        sap.m.MessageToast.show("Data Saved Successfully.");
    },
    formatDate: function(data) {
        if (data) {
            jQuery.sap.require("sap.ui.core.format.DateFormat");
            var date = new Date(data);
            return sap.ui.core.format.DateFormat.getDateInstance({style: "full", pattern: "M/d/yy"}).format(date);
        }else {
            return '';
        }
    }


});
