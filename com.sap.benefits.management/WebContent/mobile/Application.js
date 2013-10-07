jQuery.sap.declare("Application");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    init: function() {
        var employeesModel = new sap.ui.model.json.JSONModel();
        employeesModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testData.json", null, false);

        var benefitsModel = new sap.ui.model.json.JSONModel();
        benefitsModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testDataBenefits.json", null, false);

        var campaignModel = new sap.ui.model.json.JSONModel();
        campaignModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testDataCampaigns.json", null, false);

        sap.ui.getCore().setModel(employeesModel, "employeesModel");
        sap.ui.getCore().setModel(benefitsModel, "benefitsModel");
        sap.ui.getCore().setModel(campaignModel, "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignDetailsModel");
    },
    showDetails: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("employeesModel");
        sap.ui.getCore().byId("EmployeesDetails").byId("EmployeesDetailsPage").setTitle(bindingCtx.getObject().id + " Details")
        sap.ui.getCore().getModel("employeeDetailsModel")
                .setData({
            current: bindingCtx.getObject().orders.current,
            history: bindingCtx.getObject().orders.history});
    },
    campaignItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("campaignModel");
        sap.ui.getCore().getModel("campaignDetailsModel")
                .setData({
            campaign: bindingCtx.getObject()});
    },
    benefitItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("benefitsModel");
        var model = new sap.ui.model.json.JSONModel(bindingCtx.getObject());

        sap.ui.getCore().byId("BenefitsDetails").setModel(model);
    },
    goHome: function() {
        var homePage = sap.ui.getCore().byId("HomePage");
        this._getShell().setApp(homePage);
    },
    main: function() {
        var root = this.getRoot();


        var tileContainer = new sap.m.TileContainer("HomePage", {
            tiles: [
                new sap.m.StandardTile("Employees", {
                    icon: "sap-icon://employee",
                    number: "39",
                    title: "Employees",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }),
                new sap.m.StandardTile("Benefits", {
                    icon: "sap-icon://competitor",
                    number: "39",
                    title: "Benefits",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }),
                new sap.m.StandardTile("Campaigns", {
                    icon: "sap-icon://marketing-campaign",
                    title: "Campaigns",
                    info: "7 Days Left",
                    infoState: "Success",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }),
                new sap.m.StandardTile("Reports", {
                    icon: "sap-icon://travel-expense-report",
                    title: "Reports",
                    press: jQuery.proxy(this._handleTilePressed, this)
                })
            ]
        });


        var emplMasterView = sap.ui.xmlview("EmployeesMaster", "com.sap.benefits.management.view.EmployeesMaster");
        var emplDetailsView = sap.ui.xmlview("EmployeesDetails", "com.sap.benefits.management.view.EmployeesDetails");

        var benefitsMasterView = sap.ui.xmlview("BenefitsMaster", "com.sap.benefits.management.view.BenefitsMaster");
        var benefitsDetailsView = sap.ui.xmlview("BenefitsDetails", "com.sap.benefits.management.view.BenefitsDetails");

        var campaignMasterView = sap.ui.xmlview("CampaignMaster", "com.sap.benefits.management.view.CampaignsMaster");
        var campaignDetailsView = sap.ui.xmlview("CampaignDetails", "com.sap.benefits.management.view.CampaignsDetails");

        var splitApp = new sap.m.SplitApp("SplitAppControl");
        splitApp.addMasterPage(emplMasterView);
        splitApp.addDetailPage(emplDetailsView);

        splitApp.addMasterPage(benefitsMasterView);
        splitApp.addDetailPage(benefitsDetailsView);

        splitApp.addMasterPage(campaignMasterView);
        splitApp.addDetailPage(campaignDetailsView);

        var oShell = new sap.m.Shell("ShellControl", {
            title: "SAP Benefits App",
            app: tileContainer,
            showLogout: false
        });

        oShell.placeAt(root);
    },
    _getShell: function() {
        return sap.ui.getCore().byId("ShellControl");
    },
    _handleTilePressed: function(evt) {
        var splitApp = sap.ui.getCore().byId("SplitAppControl");
        switch (evt.getParameters().id) {
            case "Employees":
                splitApp.toMaster("EmployeesMaster");
                splitApp.toDetail("EmployeesDetails");
                this._getShell().setApp(splitApp);
                break;
            case "Benefits":
                splitApp.toMaster("BenefitsMaster");
                splitApp.toDetail("BenefitsDetails");
                this._getShell().setApp(splitApp);
                break;
            case "Campaigns":
                splitApp.hideMaster();
                splitApp.toMaster("CampaignMaster");
                splitApp.toDetail("CampaignDetails");
                this._getShell().setApp(splitApp);
                break;
            default:
        }
    }
});
