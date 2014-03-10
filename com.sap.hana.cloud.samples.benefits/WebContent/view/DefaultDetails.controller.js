sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.DefaultDetails", {
	onInit : function() {

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
	}
});
