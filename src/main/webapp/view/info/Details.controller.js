jQuery.sap.require("sap.m.MessageBox");
jQuery.sap.require("com.sap.hana.cloud.samples.benefits.util.AjaxUtil");

sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.info.Details", {

	onInit : function() {
		this.getView().setModel(new sap.ui.model.json.JSONModel());
		this.getView().setModel(new sap.ui.model.json.JSONModel(), "empPhoto");
		this.getView().setModel(new sap.ui.model.json.JSONModel(), "hrPhoto");
	},
	
	onBeforeRendering : function() {
		this.loadModels();
		this.hideLogout();
	},

	loadModels : function() {
		var empModel = this.getView().getModel();
		var empPhotoModel = this.getView().getModel("empPhoto");
		var hrPhotoModel = this.getView().getModel("hrPhoto");
		
		if(!jQuery.isEmptyObject(empModel.getData())){
			return;
		}
		
		function createDoneCallback(model) {
			return function(data, textStatus, jqXHR) {
				model.setData(data);
			};
		};

		var hideHrPanelIfHrInfoMissing = function() {
			this.byId("hrProfilePanel").setVisible(!!empModel.getProperty("/d/results/1"));
		};

		var failCallback = function(jqXHR, textStatus, errorThrown) {
			sap.m.MessageBox.show("{b_i18n>ERROR_GETTING_USER_DETAILS}", sap.m.MessageBox.Icon.ERROR, "{b_i18n>ERROR_TITLE}",
					[sap.m.MessageBox.Action.OK], function(oAction) {
						sap.ui.getCore().getEventBus().publish("nav", "home");
					});
		};

		var alwaysCallback = function(data, textStatus, jqXHR) {
			this.byId("infoMainLayout").setBusy(false);
		};

		this.byId("infoMainLayout").setBusy(true);
		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, "OData.svc/userInfo",
				createDoneCallback(empModel), failCallback, alwaysCallback).always(hideHrPanelIfHrInfoMissing);

		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, "OData.svc/userPhoto?photoType=1",
				createDoneCallback(empPhotoModel));

		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchGetJSON(this, "OData.svc/hrPhoto?photoType=3",
				createDoneCallback(hrPhotoModel));
	},

	onNavPressed : function() {
		sap.ui.getCore().getEventBus().publish("nav", "home");
	},

	hideLogout : function() {
		this.byId("logoutButton").setVisible(appController._hasLogoutButton());
	},

	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	},

	setPhoto : function(imgData) {
		return imgData ? "data:image/png;base64," + imgData : "img/img_anon.jpg";
	}

});
