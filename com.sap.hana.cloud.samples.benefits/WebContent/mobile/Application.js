jQuery.sap.declare("Application");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
//    CAMPAIGN_MASTER_VIEW_ID: "CampaignMaster",
//    EMPLOYEE_MASTER_VIEW_ID: "EmployeesMaster",
//    BENEFITS_MASTER_VIEW_ID: "BenefitsMaster",
//    EMPLOYEE_ORDERS_MASTER_VIEW_ID: "EmployeeOrdersMaster",
//    CAMPAIGN_DETAILS_VIEW_ID: "CampaignDetails",
//    BENEFITS_DETAILS_VIEW_ID: "BenefitsDetails",
//    DEFAULT_DETAILS_VIEW_ID: "DefaultDetails",
//    EMPLOYEE_ORDERS_DETAILS_VIEW_ID: "EmployeeOrdersDetails",
    init: function() {
        // subscribe to event bus
        var bus = sap.ui.getCore().getEventBus();
        bus.subscribe("nav", "to", this.navToHandler, this);
        bus.subscribe("nav", "home", this.goHome, this);
    },
    navToHandler: function(channelId, eventId, data) {
        if (data && data.id) {
            this.openDefaultDetailsPage();
            this._toDetailsPage(data.id, {
                context: data.context,
                additionalData: data.additionalData,
            });
        } else {
            jQuery.sap.log.error("nav-to event cannot be processed. Invalid data: " + data);
        }
    },
    goHome: function() {
        var homePage = sap.ui.getCore().byId("HomePage");
        this._getShell().setApp(homePage);
    },
    openDefaultDetailsPage: function() {
        this._toDetailsPage(views.DEFAULT_DETAILS_VIEW_ID);
    },
    main: function() {
        var root = this.getRoot();
        var splitApp = new sap.m.SplitApp("SplitAppControl");
        splitApp.setBusyIndicatorDelay(0);
        var tileContainer = new sap.m.TileContainer("HomePage");

        var configData = this.getConfig().getData();

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

        splitApp.addDetailPage(sap.ui.xmlview(views.DEFAULT_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.DefaultDetails"));

        var oShell = new sap.m.Shell("ShellControl", {
            title: "SAP Benefits App",
            app: tileContainer,
            showLogout: false
        });

        oShell.placeAt(root);
    },
    _showEmployeeTile: function(app, tileContainer) {
        app.addMasterPage(sap.ui.xmlview(views.EMPLOYEE_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.employees.Master"));
        this._addOrdersDetailPageToApp(app);

        tileContainer.addTile(new sap.m.StandardTile("Employees", {
            icon: "sap-icon://employee",
            title: "Employees",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _showBenefitsTile: function(app, tileContainer) {
        app.addMasterPage(sap.ui.xmlview(views.BENEFITS_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.benefits.Master"));
        app.addDetailPage(sap.ui.xmlview(views.BENEFITS_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.benefits.Details"));

        tileContainer.addTile(new sap.m.StandardTile("Benefits", {
            icon: "sap-icon://competitor",
            title: "Benefits",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _showCampaignTile: function(app, tileContainer) {
        app.addMasterPage(sap.ui.xmlview(views.CAMPAIGN_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.campaigns.Master"));
        app.addDetailPage(sap.ui.xmlview(views.CAMPAIGN_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.campaigns.Details"));

        tileContainer.addTile(new sap.m.StandardTile("Campaigns", {
            icon: "sap-icon://marketing-campaign",
            title: "Campaigns",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _showOrdersTile: function(app, tileContainer) {
        var masterPage = sap.ui.xmlview(views.EMPLOYEE_ORDERS_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.orders.Master");
        masterPage.setModel(new sap.ui.model.json.JSONModel());
        app.addMasterPage(masterPage);
        this._addOrdersDetailPageToApp(app);

        tileContainer.addTile(new sap.m.StandardTile("Orders", {
            icon: "sap-icon://customer-order-entry",
            title: "Orders",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _addOrdersDetailPageToApp: function(app) {
        if (!app.getDetailPage(views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID)) {
            var emplOrdersDetailsView = sap.ui.xmlview(views.EMPLOYEE_ORDERS_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.orders.Details");
            app.addDetailPage(emplOrdersDetailsView);
            emplOrdersDetailsView.setModel(new sap.ui.model.json.JSONModel());
        }
    },
    _getShell: function() {
        return sap.ui.getCore().byId("ShellControl");
    },
    setAppBusy: function(busy) {
        sap.ui.getCore().byId("SplitAppControl").setBusy(busy);
    },
    _handleTilePressed: function(evt) {
        var splitApp = sap.ui.getCore().byId("SplitAppControl");
        switch (evt.getParameters().id) {
            case "Employees":
                splitApp.toMaster(views.EMPLOYEE_MASTER_VIEW_ID);
                splitApp.toDetail(sap.ui.getCore().byId(views.DEFAULT_DETAILS_VIEW_ID), "show");
                this._getShell().setApp(splitApp);
                break;
            case "Benefits":
                splitApp.toMaster(views.BENEFITS_MASTER_VIEW_ID);
                splitApp.toDetail(sap.ui.getCore().byId(views.DEFAULT_DETAILS_VIEW_ID), "show");
                this._getShell().setApp(splitApp);
                break;
            case "Campaigns":
                splitApp.toMaster(views.CAMPAIGN_MASTER_VIEW_ID);
                splitApp.toDetail(sap.ui.getCore().byId(views.DEFAULT_DETAILS_VIEW_ID), "show");
                this._getShell().setApp(splitApp);
                break;
            case "Orders":
                splitApp.toMaster(views.EMPLOYEE_ORDERS_MASTER_VIEW_ID);
                splitApp.toDetail(sap.ui.getCore().byId(views.DEFAULT_DETAILS_VIEW_ID), "show");
                this._getShell().setApp(splitApp);
                break;
            default:
        }
    },
    _toDetailsPage: function(pageId, data) {
        var splitApp = sap.ui.getCore().byId("SplitAppControl");
        splitApp.toDetail(sap.ui.getCore().byId(pageId), "show", data);
    }

});
