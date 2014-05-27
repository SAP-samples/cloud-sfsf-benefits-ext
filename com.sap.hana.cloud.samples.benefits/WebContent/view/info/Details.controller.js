sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.info.Details", {
	onBeforeRendering : function() {
		this.loadModel();
		this.hideLogout();
		this.hideHrObjectHeader();
	},

	loadModel : function() {
		if (!this.getView().getModel()) {
			this.getView().setModel(new sap.ui.model.json.JSONModel());
			this.getView().getModel().loadData("OData.svc/userInfo", null, false);
		}

	},

	onNavPressed : function() {
		sap.ui.getCore().getEventBus().publish("nav", "home");
	},

	hideLogout : function() {
		this.byId("logoutButton").setVisible(appController._hasLogoutButton());
	},

	hideHrObjectHeader : function() {
		if (this.getView().getModel().getData().d.results[1] === undefined) {
			this.byId("hrObjectHeader").setVisible(false);
		}
	},

	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	}

});
