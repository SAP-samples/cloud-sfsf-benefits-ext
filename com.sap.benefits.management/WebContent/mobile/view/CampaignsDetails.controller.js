sap.ui.controller("com.sap.benefits.management.view.CampaignsDetails", {
    onInit: function() {

    },
    onAfterRendering: function() {
    },
    saveButtonPressed: function(evt) {
        jQuery.sap.require("sap.m.MessageToast");
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
