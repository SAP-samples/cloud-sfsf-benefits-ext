sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Details", {
	onInit : function() {
		sap.ui.getCore().getEventBus().subscribe("app", "benefitsDetailsRefresh", this._refreshHandler, this);
	},
	onAfterRendering : function() {
	},
	onBeforeRendering : function() {
		this.hideLogout();
	},
	
	hideLogout : function(){
		this.byId("logoutButton").setVisible(appController._hasLogoutButton());
	},	
	
	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	},
	
	formatValue : function(value){
		var message = sap.ui.getCore().getModel("b_i18n").getProperty("BENEFITS_VALUE").formatPropertyMessage(value);
		return message;
	},
	_refreshHandler : function(channelId, eventId, data) {
		this.getView().setBindingContext(data.context);
		this.getView().setModel(data.context.getModel());
	},
});
