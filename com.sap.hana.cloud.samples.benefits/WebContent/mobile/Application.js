jQuery.sap.declare("Application");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    CAMPAIGN_MASTER_VIEW_ID : "CampaignMaster",
    EMPLOYEE_MASTER_VIEW_ID : "EmployeesMaster",
    BENEFITS_MASTER_VIEW_ID : "BenefitsMaster",
    CAMPAIGN_DETAILS_VIEW_ID : "CampaignDetails",
    EMPLOYEE_DETAILS_VIEW_ID : "EmployeeOrdersDetails",
    BENEFITS_DETAILS_VIEW_ID : "BenefitsDetails",
    DEFAULT_DETAILS_VIEW_ID : "DefaultDetails",
    
    init : function() {

        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "activeCampaignModel");

        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "managedEmployees");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeOrderDetailsModel");
        
        // Loads the logged in user profile
        sap.ui.getCore().getModel("employeeDetailsModel").loadData("../api/user/profile", null, false);

        this.reloadCampaignModel();
        this.reloadManagedEmployeesModel();
        this.reloadBenefitsModel();
    },
    
    
    
    reloadBenefitsModel : function() {
        sap.ui.getCore().getModel("benefitsModel").loadData("../api/benefits/all", null, false);
    },
    
    reloadActiveCampaign : function() {
        var campaigns = sap.ui.getCore().getModel("campaignModel").getData();
        for (var i = 0; i < campaigns.length; i++) {
            if (campaigns[i].active === true) {
                sap.ui.getCore().getModel("activeCampaignModel").setData(campaigns[i]);
            }
        }
    },

    reloadCampaignModel : function() {
        sap.ui.getCore().getModel("campaignModel").loadData("../api/campaigns/", null, false);
        // Set active campaign
        this.reloadActiveCampaign();
        // Reload available points of managed employees
        this.reloadManagedEmployeesModel();
    },

    getCampaignId : function() {
        return sap.ui.getCore().getModel("activeCampaignModel").getProperty("/id");
    },

    reloadManagedEmployeesModel : function() {
        sap.ui.getCore().getModel("managedEmployees").loadData("../api/user/managed", null, false);
        var employeesTile = sap.ui.getCore().byId("Employees");
        if (employeesTile) {
            employeesTile.setNumber(sap.ui.getCore().getModel("managedEmployees").getData().length);
        }
    },
    reloadOrdersModel : function(campaignId) {
        reloadOrdersModel();
    },

    getSelecedUserID : function() {
        return sap.ui.getCore().getModel("employeeOrderDetailsModel").getProperty("/employee/userId");
    },

    reloadOrdersModel : function() {
        var campaignId = this.getCampaignId();
        var userId = this.getSelecedUserID();
        if (campaignId) {
            jQuery.ajax({
                async : false,
                url : "../api/orders/for-user/" + campaignId + "/" + userId,
                type : 'GET',
                success : function(data) {
                    sap.ui.getCore().getModel("employeeOrderDetailsModel").setProperty("/currentOrder", data);
                }
            });
        }
    },

    employeeItemSelected : function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("managedEmployees");
        sap.ui.getCore().getModel("employeeOrderDetailsModel").setProperty("/employee", bindingCtx.getObject());
        this.reloadOrdersModel();
        if (this.getCampaignId()) {
            this._toDetailsPage(this.EMPLOYEE_DETAILS_VIEW_ID);
        } else { // No data case
            this._toDetailsPage(this.DEFAULT_DETAILS_VIEW_ID);
        }
    },

    campaignItemSelected : function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("campaignModel");
        sap.ui.getCore().byId(this.CAMPAIGN_DETAILS_VIEW_ID).setModel(bindingCtx.getModel());
        this.openDefaultDetailsPage();
        this._toDetailsPage(this.CAMPAIGN_DETAILS_VIEW_ID, {
            context : bindingCtx
        });
    },

    selectListItem : function(list, itemIndex) {
        var items = list.getItems();
        if (items[itemIndex]) {
            items[itemIndex].setSelected(true);
            list.fireSelect({
                listItem : items[itemIndex],
                id : list.getId()
            });
        } else {
            this.openDefaultDetailsPage();
        }
    },
    benefitItemSelected : function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("benefitsModel");
        var model = new sap.ui.model.json.JSONModel(bindingCtx.getObject());

        sap.ui.getCore().byId(this.BENEFITS_DETAILS_VIEW_ID).setModel(model);

        this._toDetailsPage(this.BENEFITS_DETAILS_VIEW_ID);
    },
    goHome : function() {
        var homePage = sap.ui.getCore().byId("HomePage");
        this._getShell().setApp(homePage);
    },
    openDefaultDetailsPage : function() {
        this._toDetailsPage(this.DEFAULT_DETAILS_VIEW_ID);
    },
    search: function(list, searchField, property) {
        appController.openDefaultDetailsPage();
        var showSearch = (searchField.getValue().length !== 0);
        var binding = list.getBinding("items");

        if (binding) {
            if(showSearch){
            var filterName = new sap.ui.model.Filter(property, sap.ui.model.FilterOperator.Contains, searchField.getValue());
            binding.filter([filterName]);
            }else {
                binding.filter([]);
            }
        }
    },
    main : function() {
        var root = this.getRoot();
        var managedEmployees = 0;
        var managedEmployeesModel = sap.ui.getCore().getModel("managedEmployees");
        if (managedEmployeesModel) {
            managedEmployees = managedEmployeesModel.getData().length;
        }
        var tileContainer = new sap.m.TileContainer("HomePage", {
            tiles : [ new sap.m.StandardTile("Employees", {
                icon : "sap-icon://employee",
                number : managedEmployees,
                title : "Employees",
                press : jQuery.proxy(this._handleTilePressed, this)
            }), new sap.m.StandardTile("Benefits", {
                icon : "sap-icon://competitor",
                title : "Benefits",
                press : jQuery.proxy(this._handleTilePressed, this)
            }), new sap.m.StandardTile("Campaigns", {
                icon : "sap-icon://marketing-campaign",
                title : "Campaigns",
                press : jQuery.proxy(this._handleTilePressed, this)
            }) ]
        });

        var emplMasterView = sap.ui.xmlview(this.EMPLOYEE_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.employees.Master");
        var emplDetailsView = sap.ui.xmlview(this.EMPLOYEE_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.orders.Details");
        emplDetailsView.setModel(sap.ui.getCore().getModel("employeeOrderDetailsModel"));
        var benefitsMasterView = sap.ui.xmlview(this.BENEFITS_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.benefits.Master");
        var benefitsDetailsView = sap.ui.xmlview(this.BENEFITS_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.benefits.Details");
        var campaignMasterView = sap.ui.xmlview(this.CAMPAIGN_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.campaigns.Master");
        var campaignDetailsView = sap.ui.xmlview(this.CAMPAIGN_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.campaigns.Details");
        var defaultDetailsView = sap.ui.xmlview(this.DEFAULT_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.DefaultDetails");

        var splitApp = new sap.m.SplitApp("SplitAppControl");
        splitApp.setBusyIndicatorDelay(0);

        splitApp.addMasterPage(emplMasterView);
        splitApp.addDetailPage(emplDetailsView);

        splitApp.addMasterPage(benefitsMasterView);
        splitApp.addDetailPage(benefitsDetailsView);

        splitApp.addMasterPage(campaignMasterView);
        splitApp.addDetailPage(campaignDetailsView);

        splitApp.addDetailPage(defaultDetailsView);

        var oShell = new sap.m.Shell("ShellControl", {
            title : "SAP Benefits App",
            app : tileContainer,
            showLogout : false
        });

        oShell.placeAt(root);
    },
    _getShell : function() {
        return sap.ui.getCore().byId("ShellControl");
    },
    setAppBusy : function(busy) {
        sap.ui.getCore().byId("SplitAppControl").setBusy(busy);
    },
    _handleTilePressed : function(evt) {
        var splitApp = sap.ui.getCore().byId("SplitAppControl");
        switch (evt.getParameters().id) {
        case "Employees":
            splitApp.toMaster(this.EMPLOYEE_MASTER_VIEW_ID);
            splitApp.toDetail(sap.ui.getCore().byId(this.DEFAULT_DETAILS_VIEW_ID), "show");
            this._getShell().setApp(splitApp);
            break;
        case "Benefits":
            splitApp.toMaster(this.BENEFITS_MASTER_VIEW_ID);
            splitApp.toDetail(sap.ui.getCore().byId(this.DEFAULT_DETAILS_VIEW_ID), "show");
            this._getShell().setApp(splitApp);
            break;
        case "Campaigns":
            splitApp.hideMaster();
            splitApp.toMaster(this.CAMPAIGN_MASTER_VIEW_ID);
            splitApp.toDetail(sap.ui.getCore().byId(this.DEFAULT_DETAILS_VIEW_ID), "show");
            this._getShell().setApp(splitApp);
            break;
        default:
        }
    },
    _toDetailsPage : function(pageId, data) {
        var splitApp = sap.ui.getCore().byId("SplitAppControl");
        splitApp.toDetail(sap.ui.getCore().byId(pageId), "show", data);
    }

});
