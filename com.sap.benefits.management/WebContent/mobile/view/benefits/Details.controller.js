sap.ui.controller("com.sap.benefits.management.view.benefits.Details", {
    onInit: function() {
    },
    onAfterRendering: function() {
    },
    linkPressed: function(evt) {
        var control = sap.ui.getCore().byId(evt.getParameters().id);
        var link = control.getModel().getData().link;
        sap.m.URLHelper.redirect(link, true);
    }

});
