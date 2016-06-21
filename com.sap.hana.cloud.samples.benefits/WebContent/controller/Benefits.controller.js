sap.ui.define([
    "com/sap/hana/cloud/samples/benefits/controller/Base.controller"
], function (Controller) {
    "use strict";

    return Controller.extend("com.sap.hana.cloud.samples.benefits.controller.Benefits", {
        detailsTargetName: "benefit",
        defaultModelName: "benefitInfos"
    });

}, true);