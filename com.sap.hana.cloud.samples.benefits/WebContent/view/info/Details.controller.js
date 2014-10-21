jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.info.Details", {

	anonymousPhoto : "img/img_anon.jpg",

	onBeforeRendering : function() {
		this.loadModel();
		this.hideLogout();
		this.hideHrProfilePanel();
	},

	loadModel : function() {
		if (!this.getView().getModel()) {
			this._initModelSync("OData.svc/userInfo").fail(
					function(jqXHR, textStatus, errorThrown) {
						sap.m.MessageBox.show("{b_i18n>ERROR_GETTING_USER_DETAILS}", sap.m.MessageBox.Icon.ERROR,
								"{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK], function(oAction) {
									sap.ui.getCore().getEventBus().publish("nav", "home");
								});
					});

			this._initModelSync("OData.svc/userPhoto?photoType=1", "empPhoto");
			this._initModelSync("OData.svc/hrPhoto?photoType=3", "hrPhoto");
		}
	},

	_initModelSync : function(url, modelName) {
		return jQuery.ajax({
			url : url,
			async : false,
			context : this,
			dataType : "json",
		}).done(function(data, textStatus, jqXHR) {
			this.getView().setModel(new sap.ui.model.json.JSONModel(data), modelName);
		});
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

	hideHrProfilePanel : function() {
		var model = this.getView().getModel();
		if (model && !model.getData().d.results[1]) {
			this.byId("hrProfilePanel").setVisible(false);
		}
	},

	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	},

	_setPhoto : function(imgEl, modelName, propName) {
		var userPhotoEl = $("#" + imgEl);
		var model = this.getView().getModel(modelName);
		var photoData = model && model.getData().d[propName];
		var src = photoData ? "data:image/png;base64," + photoData : this.anonymousPhoto;

		userPhotoEl.attr("src", src);
	}

});
