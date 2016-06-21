sap.ui.define([
    "com/sap/hana/cloud/samples/benefits/controller/Base.controller"
], function (Controller) {
    "use strict";

    function getWidget() {
        var hash = location.hash;
        if (hash.indexOf("/benefits")) {
            return {
                model: "benefitInfos",
                route: "benefit",
                page: "BenefitsDetailsPage"
            };
        } else if (hash.indexOf("/campaign")) {
            return {
                modelName: "hrCampaigns",
                route: "campaign",
                page: "CampaignDetailsPage"
            };
        }
    }

    return Controller.extend("com.sap.hana.cloud.samples.benefits.controller.Main", {
        onInit: function() {
            var oComponent = this.getOwnerComponent();
            this.b_i18n = oComponent.getModel('b_i18n');
            this.benefitInfos = oComponent.getModel('benefitInfos');
            this.hrCampaigns = oComponent.getModel('hrCampaigns');
            this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            this.oRouter.attachRouteMatched(this.onRouteMatched, this);
        },
        onTilePress: function(oEvt) {
            var oTile = oEvt.getSource(),
                oPath = oTile.getBindingContext("tilesModel").getPath(),
                sId = oTile.getModel("tilesModel").getProperty(oPath + "/id");

            this.oRouter.navTo(sId.split("_")[0].toLowerCase());
        },
        onBackPress: function() {
            this.oRouter.navTo("tiles");
        },
        i18n: function(sMessage) {
            return this.b_i18n.getProperty(sMessage);
        },
        setPhoto : function(imgData) {
            return "data:image/png;base64," + imgData;
        },
        setState: function(active) {
            return active ? sap.ui.core.ValueState.Success : sap.ui.core.ValueState.Error;
        },
        setStateText: function (active) {
            return this.b_i18n.getProperty(active ? "CAMPAIGN_STATUS_ACTIVE" : "CAMPAIGN_STATUS_INACTIVE");
        },
        formatDate: function(date) {
            // TODO
            return date;
        },
        formatStateText: function(text) {
            // TODO
            return text;
        },
        handleSearch: function(oEvt) {
            // TODO
        },
        linkPressed: function(oEvt) {
            // TODO
        },
        onItemSelected: function(oEvt) {
            var oWidget = getWidget(),
                oSource = oEvt.getSource(),
                oPath = oSource.getSelectedContextPaths(),
                sId = oSource.getModel(oWidget.model).getProperty(oPath + "/Id");

            this.oRouter.navTo(oWidget.route, {id: sId});
        },
        onRouteMatched: function(oEvt) {
            var oWidget = getWidget(),
                sModel = oWidget.model,
                oPage = this.getView().byId(oWidget.page),
                oData,
                sId = oEvt.getParameters().arguments.id;

            function bind(sId) {
                var sIdx = oData.d.results
                    .map(function(item) { return item.Id; })
                    .indexOf(sId);
                oPage.bindElement("/d/results/" + sIdx);
            }

            if (oPage) {
                oPage.setModel(this[sModel]);
                oData = this[sModel].getData();
                if(Object.keys(oData).length) {
                    bind(sId);
                } else { // in case model is not ready yet, wait until it will be ready
                    oPage.setBusyIndicatorDelay(1000).setBusy(true);
                    this[sModel].attachRequestCompleted(function() {
                        oPage.setBusy(false);
                        // read data once again, since earlier it was empty
                        oData = this[sModel].getData();
                        bind(sId);
                    }, this);
                }
            }
        }
    });

});