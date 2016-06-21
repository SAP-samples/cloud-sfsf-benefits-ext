sap.ui.define([
    "com/sap/hana/cloud/samples/benefits/controller/Base.controller",
    "sap/ui/core/ValueState",
    "sap/ui/core/LocaleData"
], function (Controller, ValueState, LocaleData) {
    "use strict";

    var oConfiguration = sap.ui.getCore().getConfiguration(),
        oLocaleData = LocaleData.getInstance(oConfiguration.getFormatSettings().getFormatLocale()),
        sCalendarType = oConfiguration.getCalendarType();

    return Controller.extend("com.sap.hana.cloud.samples.benefits.controller.Campaign", {
        detailsTargetName: "campaign",
        defaultModelName: "hrCampaigns",
        formatState: function(bActive) {
            return bActive ? ValueState.Success : ValueState.Error;
        },
        formatStateText: function(bActive) {
            return this.b_i18n.getProperty(bActive ? "CAMPAIGN_STATUS_ACTIVE" : "CAMPAIGN_STATUS_INACTIVE");
        },
        formatDate: function(sDate) {
            if (sDate) {
                var dDate = new Date(parseInt(sDate.substr(6, 13), 10)),
                    sMonth = oLocaleData.getMonths("abbreviated", sCalendarType)[dDate.getMonth()];
                return sMonth + ' ' + dDate.getDate() + ', ' + dDate.getFullYear();
            } else {
                return this.b_i18n.getProperty("NOT_SET_MSG");
            }
        }
    });

}, true);