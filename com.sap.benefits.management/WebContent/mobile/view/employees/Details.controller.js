sap.ui.controller("com.sap.benefits.management.view.employees.Details", {
    monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
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
    formatAvailablePoints: function(campaignPoints,usedPoints) {
        var result = campaignPoints - usedPoints;     
        return result.toString(10) + " Points";
    },
//    groupBenefits: function(oContext) {
//        var date = new Date(oContext.getProperty("date"));
//        return {
//            key: date.getMonth() + 1,
//            text: this.monthNames[date.getMonth()]
//        };
//    },
    linkPressed : function(evt){
        var sourceControl = evt.getSource();
        var model = sourceControl.getModel();
        var contextPath = sourceControl.getBindingContext().sPath+"/benefitDetails/infoLink";
        var link = model.getProperty(contextPath);
        sap.m.URLHelper.redirect(link, true);
    }
});
