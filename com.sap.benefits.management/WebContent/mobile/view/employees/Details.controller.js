sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.employees.Details", {
    onInit: function() {
    },
    onAfterRendering: function() {
    },
    formatBenefitItemsSum: function(benefitItems) {
        var result = 0;
        for (benefitItem in benefitItems) {
            result += benefitItems[benefitItem].quantity * benefitItems[benefitItem].itemValue;
        }
        return result;
    },
    formatAvailablePoints: function(campaignPoints, usedPoints) {
        var result = campaignPoints - usedPoints;
        return result.toString(10) + " Points";
    },
    linkPressed: function(evt) {
        var sourceControl = evt.getSource();
        var model = sourceControl.getModel();
        var contextPath = sourceControl.getBindingContext().sPath + "/benefitDetails/infoLink";
        var link = model.getProperty(contextPath);
        sap.m.URLHelper.redirect(link, true);
    }
});
