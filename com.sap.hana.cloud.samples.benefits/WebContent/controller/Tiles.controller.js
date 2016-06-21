sap.ui.define([
    "com/sap/hana/cloud/samples/benefits/controller/Base.controller"
], function (Controller) {
    "use strict";

    return Controller.extend("com.sap.hana.cloud.samples.benefits.controller.Tiles", {
        defaultModelName: "tilesModel",
        onTilePress: function(oEvt) {
            var oTile = oEvt.getSource(),
                oPath = oTile.getBindingContext().getPath(),
                sId = oTile.getModel().getProperty(oPath + "/id");

            this.oRouter.navTo(sId.split("_")[0].toLowerCase());
        }
    });

}, true);