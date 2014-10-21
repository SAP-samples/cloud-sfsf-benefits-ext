jQuery.sap.declare("Application");
jQuery.sap.require("sap.ui.app.Application");
jQuery.sap.require("sap.m.MessageBox");

sap.ui.app.Application.extend("Application", {
	init : function() {
		// subscribe to event bus
		var bus = sap.ui.getCore().getEventBus();
		bus.subscribe("nav", "to", this._navToHandler, this);
		bus.subscribe("nav", "home", this._goHome, this);
		var i18nModel = new sap.ui.model.resource.ResourceModel({
			bundleUrl : jQuery.sap.getModulePath("com.sap.hana.cloud.samples.benefits") + "/i18n/i18n.properties"
		});

		sap.ui.getCore().setModel(i18nModel, "b_i18n");

	},
	setAppBusy : function(busy) {
		sap.ui.getCore().byId("SplitAppControl").setBusy(busy);
	},
	main : function() {
		var root = this.getRoot();
		var splitApp = new sap.m.SplitApp("SplitAppControl");
		splitApp.setBusyIndicatorDelay(0);
		splitApp.addDetailPage(sap.ui.xmlview(views.DEFAULT_DETAILS_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.DefaultDetails"));
		var tileContainer = new sap.m.TileContainer("HomePage");

		var configData = this.getConfig().getData().d.uiConfig;

		if (configData.showEmployeesTile) {
			this._showEmployeeTile(splitApp, tileContainer);
		}

		if (configData.showBenefitsTile) {
			this._showBenefitsTile(splitApp, tileContainer);
		}

		if (configData.showCampaignTile) {
			this._showCampaignTile(splitApp, tileContainer);
		}

		if (configData.showOrderTile) {
			this._showOrdersTile(splitApp, tileContainer);
		}

		if (configData.showInfoTile) {
			this._showInfoTile(tileContainer);
		}

		var logoutButton = new sap.m.Button({
			text : "{b_i18n>LOGOUT}",
			icon : "sap-icon://log",
			press : jQuery.proxy(this.onLogout, this),
			visible : this._hasLogoutButton()
		});

		var aPage = new sap.m.Page({
			id : "MyHome",
			showHeader : true,
			enableScrolling : false,
			content : [tileContainer],
			headerContent : [logoutButton]
		});

		var oShell = new sap.m.Shell("ShellControl", {
			title : "{b_i18n>APPLICATION_NAME}",
			app : aPage, // tileContainer
			showLogout : false
		});

		oShell.placeAt(root);
	},
	_navToHandler : function(channelId, eventId, data) {
		if (data && data.id) {
			this._toDetailsPage(data.id, {
				context : data.context,
				additionalData : data.additionalData
			});
		} else {
			jQuery.sap.log.error("nav-to event cannot be processed. Invalid data: " + data);
		}
	},
	_goHome : function() {
		var homePage = sap.ui.getCore().byId("MyHome");// ("HomePage");
		this._getShell().setApp(homePage);
		sap.ui.getCore().byId("SplitAppControl").toDetail(views.BENEFITS_DETAILS_VIEW_ID);
	},
	_showEmployeeTile : function(app, tileContainer) {
		app.addMasterPage(sap.ui.xmlview(views.EMPLOYEE_MASTER_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.employees.Master"));
		this._addOrdersDetailPageToApp(app);

		tileContainer.addTile(new sap.m.StandardTile("Employees", {
			icon : "sap-icon://employee",
			title : "{b_i18n>EMPLOYEES_TILE_NAME}",
			press : jQuery.proxy(this._handleTilePressed, this)
		}));
	},
	_showBenefitsTile : function(app, tileContainer) {
		app.addMasterPage(sap.ui.xmlview(views.BENEFITS_MASTER_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.benefits.Master"));
		app.addDetailPage(sap.ui.xmlview(views.BENEFITS_DETAILS_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.benefits.Details"));

		tileContainer.addTile(new sap.m.StandardTile("Benefits", {
			icon : "sap-icon://competitor",
			title : "{b_i18n>BENEFITS_TILE_NAME}",
			press : jQuery.proxy(this._handleTilePressed, this)
		}));
	},
	_showCampaignTile : function(app, tileContainer) {
		app.addMasterPage(sap.ui.xmlview(views.CAMPAIGN_MASTER_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.campaigns.Master"));
		app.addDetailPage(sap.ui.xmlview(views.CAMPAIGN_DETAILS_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.campaigns.Details"));

		tileContainer.addTile(new sap.m.StandardTile("Campaigns", {
			icon : "sap-icon://marketing-campaign",
			title : "{b_i18n>CAMPAIGN_TILE_NAME}",
			press : jQuery.proxy(this._handleTilePressed, this)
		}));
	},
	_showOrdersTile : function(app, tileContainer) {
		var masterPage = sap.ui.xmlview(views.EMPLOYEE_ORDERS_MASTER_VIEW_ID,
				"com.sap.hana.cloud.samples.benefits.view.orders.Master");
		masterPage.setModel(new sap.ui.model.json.JSONModel());
		app.addMasterPage(masterPage);
		this._addOrdersDetailPageToApp(app);

		tileContainer.addTile(new sap.m.StandardTile("Orders", {
			icon : "sap-icon://customer-order-entry",
			title : "{b_i18n>ORDERS_TILE_NAME}",
			press : jQuery.proxy(this._handleTilePressed, this)
		}));
	},
	_showInfoTile : function(tileContainer) {
		sap.ui.xmlview(views.INFO_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.info.Details").addStyleClass(
				"infoPageContainer");

		tileContainer.addTile(new sap.m.StandardTile("Info", {
			icon : "sap-icon://account",
			title : "{b_i18n>INFO_TILE_NAME}",
			press : jQuery.proxy(this._handleTilePressed, this)
		}));
	},
	_addOrdersDetailPageToApp : function(app) {
		if (!app.getDetailPage(views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID)) {
			var emplOrdersDetailsView = sap.ui.xmlview(views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID,
					"com.sap.hana.cloud.samples.benefits.view.orders.Details");
			app.addDetailPage(emplOrdersDetailsView);
			emplOrdersDetailsView.setModel(new sap.ui.model.json.JSONModel());
		}
	},
	_getShell : function() {
		return sap.ui.getCore().byId("ShellControl");
	},
	_handleTilePressed : function(evt) {
		var splitApp = sap.ui.getCore().byId("SplitAppControl");
		switch (evt.getParameters().id) {
			case "Employees" :
				splitApp.toMaster(views.EMPLOYEE_MASTER_VIEW_ID);
				this._getShell().setApp(splitApp);
				break;
			case "Benefits" :
				splitApp.toMaster(views.BENEFITS_MASTER_VIEW_ID);
				splitApp.toDetail(sap.ui.getCore().byId(views.DEFAULT_DETAILS_VIEW_ID), "show");
				this._getShell().setApp(splitApp);
				break;
			case "Campaigns" :
				splitApp.toMaster(views.CAMPAIGN_MASTER_VIEW_ID);
				this._getShell().setApp(splitApp);
				break;
			case "Orders" :
				splitApp.toMaster(views.EMPLOYEE_ORDERS_MASTER_VIEW_ID);
				this._getShell().setApp(splitApp);
				break;
			case "Info" :
				this._getShell().setApp(sap.ui.getCore().byId(views.INFO_DETAILS_VIEW_ID));
				break;
			default :
		}
	},
	_toDetailsPage : function(pageId, data) {
		var splitApp = sap.ui.getCore().byId("SplitAppControl");
		splitApp.toDetail(sap.ui.getCore().byId(pageId), "show", data);
	},
	_hasLogoutButton : function() {
		return jQuery.sap.getUriParameters().get("hasLogout") === "true";
	},
	onLogout : function() {

		if (!this.logoutDialog) {
			this.logoutDialog = sap.ui.xmlfragment("logoutDialog", "view.logoutDialog", this);
		}

		sap.ui.getCore().getEventBus().publish("nav", "virtual");
		this.logoutDialog.open();
	},

	cancelLogoutButtonPressed : function() {
		this.logoutDialog.close();
	},

	okLogoutButtonPressed : function() {
		window.location.assign("logout");
	},

	onError : function(sMessage, sFile, iLine) {
		if (sMessage.indexOf(this._CONFIG_ERR_MSG) != -1) {
			sap.m.MessageBox.show("{b_i18n>APP_CFG_LOADING_FAILED}", sap.m.MessageBox.Icon.ERROR, "{b_i18n>ERROR_TITLE}",
					[sap.m.MessageBox.Action.OK]);
		}
	},

	_CONFIG_ERR_MSG : "Could not load config",
});
