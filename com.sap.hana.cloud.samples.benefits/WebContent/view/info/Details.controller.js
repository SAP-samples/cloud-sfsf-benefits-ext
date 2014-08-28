sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.info.Details", {

	anonymousPhoto : "img/img_anon.jpg",

	onBeforeRendering : function() {
		this.loadModel();
		this.hideLogout();
		this.hideHrObjectHeader();
	},

	loadModel : function() {
		if (!this.getView().getModel()) {
			this._initModel("OData.svc/userInfo");
			var employeeUserId = this.getView().getModel().getData().d.results[0].userId;
			this._initModel("OData.svc/BenefitsAmount?userId='" + employeeUserId + "'", "benefitsAmount");
			this._initModel("OData.svc/userPhoto?photoType=1", "empPhoto");
			this._initModel("OData.svc/hrPhoto?photoType=3", "hrPhoto");
		}
	},

	onAfterRendering : function() {
		this._setPhoto("InfoDetails--empImage", "empPhoto", "userPhoto");
		this._setPhoto("InfoDetails--hrImage", "hrPhoto", "hrPhoto");
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
	},

	_initModel : function(modelUrl, modelName) {
		var model = new sap.ui.model.json.JSONModel();
		model.loadData(modelUrl, null, false);

		var view = this.getView();
		modelName ? view.setModel(model, modelName) : view.setModel(model);
	},

	_setPhoto : function(imgEl, modelName, propName) {
		var userPhotoEl = $("#" + imgEl);
		var photoData = this.getView().getModel(modelName).getData().d[propName];
		var src = photoData ? "data:image/png;base64," + photoData : this.anonymousPhoto;

		userPhotoEl.attr("src", src);
	}

});
