sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Details", {
	onInit : function() {
		// this.getView().addEventDelegate({
		// onBeforeShow: function(evt) {
		// if (evt.data.context) {
		// this.setModel(evt.data.context.getModel());
		// }
		// }
		// }, this.getView());
		sap.ui.getCore().getEventBus().subscribe("app", "benefitsDetailsRefresh", this._refreshHandler, this);
	},
	onAfterRendering : function() {
	},
	linkPressed : function(evt) {
		var control = sap.ui.getCore().byId(evt.getParameters().id);
		var link = control.getModel().getData().infoLink;
		if (link) {
			sap.m.URLHelper.redirect(link, true);
		}
	},
	_refreshHandler : function(channelId, eventId, data) {
		this.getView().setBindingContext(data.context);
		this.getView().setModel(data.context.getModel());
	},
});
