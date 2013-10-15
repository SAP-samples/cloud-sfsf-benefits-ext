jQuery.sap.declare("Application");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    init: function() {

        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "activeCampaignModel");

        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "managedEmployees");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignDetailsModel");

        this.reloadCampaignModel();
        this.reloadManagedEmployeesModel();
        this.reloadBenefitsModel();
    },
    reloadBenefitsModel: function() {
        sap.ui.getCore().getModel("benefitsModel").loadData("../api/benefits/all", null, false);
    },
    reloadActiveCampaign: function() {
        var campaigns = sap.ui.getCore().getModel("campaignModel").getData();
        for (var i = 0; i < campaigns.length; i++) {
            if (campaigns[i].active === true) {
                sap.ui.getCore().getModel("activeCampaignModel").setData(campaigns[i]);
            }
        }
    },
    reloadCampaignModel: function() {
        sap.ui.getCore().getModel("campaignModel").loadData("../api/campaigns/", null, false);
        // Set active campaign
        this.reloadActiveCampaign();
        // Reload available points of managed employees
        this.reloadManagedEmployeesModel();
    },
    reloadManagedEmployeesModel: function() {
        sap.ui.getCore().getModel("managedEmployees").loadData("../api/user/managed", null, false);
        var employeesTile = sap.ui.getCore().byId("Employees");
        if (employeesTile) {
            employeesTile.setNumber(sap.ui.getCore().getModel("managedEmployees").getData().length);
        }
    },
    employeeItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("managedEmployees");
        var model = new sap.ui.model.json.JSONModel({
            employee: bindingCtx.getObject()
        });
        var campaignId = sap.ui.getCore().getModel("activeCampaignModel").getProperty("/id");
        var userId = model.getProperty("/employee/userId");
        if (campaignId) {
            jQuery.ajax({
                async: false,
                url: "../api/orders/for-user/" + campaignId + "/" + userId,
                type: 'GET',
                success: function(data) {
                    model.setProperty("/currentOrder", data);
                }
            });
        }

        sap.ui.getCore().byId("EmployeesDetails").setModel(model);
        this._toDetailsPage("EmployeesDetails");
    },
    campaignItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("campaignModel");
        sap.ui.getCore().byId("CampaignDetails").setModel(bindingCtx.getModel());
        this._toDetailsPage("DefaultDetails");
        this._toDetailsPage("CampaignDetails", {
            context: bindingCtx
        });
    },
    selectListItem: function(list, itemIndex) {
        var items = list.getItems();
        if (items[itemIndex]) {
            items[itemIndex].setSelected(true);
            list.fireSelect({
                listItem: items[itemIndex],
                id: list.getId()
            });
        }
    },
    benefitItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("benefitsModel");
        var model = new sap.ui.model.json.JSONModel(bindingCtx.getObject());

        sap.ui.getCore().byId("BenefitsDetails").setModel(model);

        this._toDetailsPage("BenefitsDetails");
    },
    goHome: function() {
        var homePage = sap.ui.getCore().byId("HomePage");
        this._getShell().setApp(homePage);
    },
    main: function() {
        var root = this.getRoot();
        var managedEmployees = 0;
        var managedEmployeesModel = sap.ui.getCore().getModel("managedEmployees");
        if (managedEmployeesModel) {
            managedEmployees = managedEmployeesModel.getData().length;
        }
        var tileContainer = new sap.m.TileContainer("HomePage", {
            tiles: [new sap.m.StandardTile("Employees", {
                    icon: "sap-icon://employee",
                    number: managedEmployees,
                    title: "Employees",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }), new sap.m.StandardTile("Benefits", {
                    icon: "sap-icon://competitor",
                    title: "Benefits",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }), new sap.m.StandardTile("Campaigns", {
                    icon: "sap-icon://marketing-campaign",
                    title: "Campaigns",
                    press: jQuery.proxy(this._handleTilePressed, this)
                })]
        });

        var emplMasterView = sap.ui.xmlview("EmployeesMaster", "com.sap.benefits.management.view.employees.Master");
        var emplDetailsView = sap.ui.xmlview("EmployeesDetails", "com.sap.benefits.management.view.employees.Details");
        var benefitsMasterView = sap.ui.xmlview("BenefitsMaster", "com.sap.benefits.management.view.benefits.Master");
        var benefitsDetailsView = sap.ui.xmlview("BenefitsDetails", "com.sap.benefits.management.view.benefits.Details");
        var campaignMasterView = sap.ui.xmlview("CampaignMaster", "com.sap.benefits.management.view.campaigns.Master");
        var campaignDetailsView = sap.ui.xmlview("CampaignDetails", "com.sap.benefits.management.view.campaigns.Details");
        var defaultDetailsView = sap.ui.xmlview("DefaultDetails", "com.sap.benefits.management.view.DefaultDetails");

        var splitApp = new sap.m.SplitApp("SplitAppControl");
        splitApp.addMasterPage(emplMasterView);
        splitApp.addDetailPage(emplDetailsView);

        splitApp.addMasterPage(benefitsMasterView);
        splitApp.addDetailPage(benefitsDetailsView);

        splitApp.addMasterPage(campaignMasterView);
        splitApp.addDetailPage(campaignDetailsView);

        splitApp.addDetailPage(defaultDetailsView);

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
                splitApp.toDetail(sap.ui.getCore().byId("DefaultDetails"), "show");
                this._getShell().setApp(splitApp);
                break;
            case "Benefits":
                splitApp.toMaster("BenefitsMaster");
                splitApp.toDetail(sap.ui.getCore().byId("DefaultDetails"), "show");
                this._getShell().setApp(splitApp);
                break;
            case "Campaigns":
                splitApp.hideMaster();
                splitApp.toMaster("CampaignMaster");
                splitApp.toDetail(sap.ui.getCore().byId("DefaultDetails"), "show");
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
