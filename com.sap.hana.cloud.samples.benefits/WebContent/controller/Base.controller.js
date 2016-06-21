sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/SplitContainer"
], function (Controller, SplitContainer) {
    "use strict";

    return Controller.extend("com.sap.hana.cloud.samples.benefits.controller.Base", {
        detailsTargetName: "",
        defaultModelName: "tilesModel",
        onInit: function() {
            var oComponent = this.getOwnerComponent();
            this.b_i18n = oComponent.getModel('b_i18n');
            this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            this.oRouter.attachRouteMatched(this.onRouteMatched, this);
            this.oModel = oComponent.getModel(this.defaultModelName);
            this.getView().setModel(this.oModel);
        },
        onBackPress: function() {
            this.oRouter.navTo("tiles");
        },
        onItemSelected: function(oEvt) {
            var oSource = oEvt.getSource(),
                oPath = oSource.getSelectedContextPaths(),
                sId = oSource.getModel().getProperty(oPath + "/Id");

            this.oRouter.navTo(this.detailsTargetName, {id: sId});
        },
        onRouteMatched: function(oEvt) {
            var oControl = this.getView().getAggregation("content")[0],
                sId = oEvt.getParameters().arguments.id,
                oData;

            function bindDetails(sId) {
                var sIdx = oData.d.results
                    .map(function(item) { return item.Id; })
                    .indexOf(sId);
                oControl.bindElement("/d/results/" + sIdx);
            }

            if (oControl instanceof SplitContainer) {
                oData = this.oModel.getData();
                if(Object.keys(oData).length) {
                    bindDetails(sId);
                } else { // in case model is not ready yet, wait until it will be ready
                    oControl.setBusyIndicatorDelay(1000).setBusy(true);
                    this.oModel.attachRequestCompleted(function() {
                        oControl.setBusy(false);
                        // read data once again, since earlier it was empty
                        oData = this.oModel.getData();
                        bindDetails(sId);
                    }, this);
                }
            }
        },
        i18n: function(sMessage) {
            return this.b_i18n.getProperty(sMessage);
        }
    });

}, true);