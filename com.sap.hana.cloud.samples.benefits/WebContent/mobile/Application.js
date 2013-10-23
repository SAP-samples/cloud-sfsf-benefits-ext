jQuery.sap.declare("Application");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    CAMPAIGN_MASTER_VIEW_ID: "CampaignMaster",
    EMPLOYEE_MASTER_VIEW_ID: "EmployeesMaster",
    BENEFITS_MASTER_VIEW_ID: "BenefitsMaster",
    EMPLOYEE_ORDERS_MASTER_VIEW_ID: "EmployeeOrdersMaster",
    CAMPAIGN_DETAILS_VIEW_ID: "CampaignDetails",
    EMPLOYEE_DETAILS_VIEW_ID: "EmployeeOrdersDetails",
    BENEFITS_DETAILS_VIEW_ID: "BenefitsDetails",
    DEFAULT_DETAILS_VIEW_ID: "DefaultDetails",
    EMPLOYEE_ORDERS_DETAILS_VIEW_ID: "EmployeeOrdersDetails",
    init: function() {
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
        sap.ui.getCore().getModel("benefitsModel").loadData("../api/benefits/all", null, false);
    },
    getSelecedUserID: function() {
        return this.selectedManagedUserId;
    },
    reloadCampaignModel: function() {
        sap.ui.getCore().getModel("campaignModel").loadData("../api/campaigns/", null, false);
    },
    getCampaignId: function() {
        return this.selectedCampaignId;
    },
    handleUserCampaignItemSelect: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext();
        this.selectedCampaignId = bindingCtx.getObject().id;
        this.selectedManagedUserId = undefined;
        this.reloadOrdersModel();
        this.openDefaultDetailsPage();
        this._toDetailsPage(this.EMPLOYEE_ORDERS_DETAILS_VIEW_ID, {
            context: bindingCtx
        });
    },
    reloadOrdersModel: function() {
        var campaignId = this.selectedCampaignId;
        var url = this.selectedManagedUserId ? "../api/orders/for-user/" + campaignId + "/" + this.selectedManagedUserId : "../api/user/orders/" + campaignId;
        if (campaignId) {
            jQuery.ajax({
                async: false,
                url: url,
                type: 'GET',
                success: jQuery.proxy(function(data) {
                    this._setOrderDataToOrderDetailsModel(data);
                }, this)
            });
        }
    },
    employeeItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext();
        this._setEmployeeDataToOrderDetailsModel(bindingCtx.getObject());
        this.selectedManagedUserId = bindingCtx.getObject().userId;
        this.selectedCampaignId = jQuery.sap.syncGetJSON("../api/campaigns/active").data.id;
        this.reloadOrdersModel();
        this._toDetailsPage(this.EMPLOYEE_DETAILS_VIEW_ID);
    },
    campaignItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("campaignModel");
        sap.ui.getCore().byId(this.CAMPAIGN_DETAILS_VIEW_ID).setModel(bindingCtx.getModel());
        this.openDefaultDetailsPage();
        this._toDetailsPage(this.CAMPAIGN_DETAILS_VIEW_ID, {
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
        } else {
            this.openDefaultDetailsPage();
        }
    },
    benefitItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("benefitsModel");
        var model = new sap.ui.model.json.JSONModel(bindingCtx.getObject());

        sap.ui.getCore().byId(this.BENEFITS_DETAILS_VIEW_ID).setModel(model);

        this._toDetailsPage(this.BENEFITS_DETAILS_VIEW_ID);
    },
    goHome: function() {
        var homePage = sap.ui.getCore().byId("HomePage");
        this._getShell().setApp(homePage);
    },
    openDefaultDetailsPage: function() {
        this._toDetailsPage(this.DEFAULT_DETAILS_VIEW_ID);
    },
    search: function(list, searchField, property) {
        appController.openDefaultDetailsPage();
        var showSearch = (searchField.getValue().length !== 0);
        var binding = list.getBinding("items");

        if (binding) {
            if (showSearch) {
                var filterName = new sap.ui.model.Filter(property, sap.ui.model.FilterOperator.Contains, searchField.getValue());
                binding.filter([filterName]);
            } else {
                binding.filter([]);
            }
        }
    },
    main: function() {
        var root = this.getRoot();
        var splitApp = new sap.m.SplitApp("SplitAppControl");
        splitApp.setBusyIndicatorDelay(0);
        var tileContainer = new sap.m.TileContainer("HomePage");

        var configData = this.getConfig().getData();

        if (configData.isAdmin) {
            sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignModel");
            this.reloadCampaignModel();
        } 

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

        splitApp.addDetailPage(sap.ui.xmlview(this.DEFAULT_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.DefaultDetails"));

        var oShell = new sap.m.Shell("ShellControl", {
            title: "SAP Benefits App",
            app: tileContainer,
            showLogout: false
        });

        oShell.placeAt(root);
    },
    _showEmployeeTile: function(app, tileContainer) {
        app.addMasterPage(sap.ui.xmlview(this.EMPLOYEE_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.employees.Master"));
        this._addOrdersDetailPageToApp(app);

        tileContainer.addTile(new sap.m.StandardTile("Employees", {
            icon: "sap-icon://employee",
            title: "Employees",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _showBenefitsTile: function(app, tileContainer) {
        app.addMasterPage(sap.ui.xmlview(this.BENEFITS_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.benefits.Master"));
        app.addDetailPage(sap.ui.xmlview(this.BENEFITS_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.benefits.Details"));

        tileContainer.addTile(new sap.m.StandardTile("Benefits", {
            icon: "sap-icon://competitor",
            title: "Benefits",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _showCampaignTile: function(app, tileContainer) {
        app.addMasterPage(sap.ui.xmlview(this.CAMPAIGN_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.campaigns.Master"));
        app.addDetailPage(sap.ui.xmlview(this.CAMPAIGN_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.campaigns.Details"));

        tileContainer.addTile(new sap.m.StandardTile("Campaigns", {
            icon: "sap-icon://marketing-campaign",
            title: "Campaigns",
            press: jQuery.proxy(this._handleTilePressed, this)
        }));
    },
    _showOrdersTile: function(app, tileContainer) {
        var masterPage = sap.ui.xmlview(this.EMPLOYEE_ORDERS_MASTER_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.orders.Master");
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
        if (!app.getDetailPage(this.EMPLOYEE_ORDERS_DETAILS_VIEW_ID)) {
            var emplOrdersDetailsView = sap.ui.xmlview(this.EMPLOYEE_ORDERS_DETAILS_VIEW_ID, "com.sap.hana.cloud.samples.benefits.view.orders.Details");
            app.addDetailPage(emplOrdersDetailsView);
            emplOrdersDetailsView.setModel(new sap.ui.model.json.JSONModel());
        }
    },
    _setOrderDataToOrderDetailsModel: function(currentOrder) {
        sap.ui.getCore().byId(this.EMPLOYEE_ORDERS_DETAILS_VIEW_ID).getModel().setProperty("/currentOrder", currentOrder);
    },
    _setEmployeeDataToOrderDetailsModel: function(employeeData) {
        sap.ui.getCore().byId(this.EMPLOYEE_ORDERS_DETAILS_VIEW_ID).getModel().setProperty("/employee", employeeData);
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
                splitApp.toMaster(this.CAMPAIGN_MASTER_VIEW_ID);
                splitApp.toDetail(sap.ui.getCore().byId(this.DEFAULT_DETAILS_VIEW_ID), "show");
                this._getShell().setApp(splitApp);
                break;
            case "Orders":
                sap.ui.getCore().byId(this.EMPLOYEE_ORDERS_MASTER_VIEW_ID).getModel().loadData("../api/user/campaigns", null, false);
                //load user profile data
                var userProfile = new sap.ui.model.json.JSONModel();
                userProfile.loadData("../api/user/profile", null, false);
                sap.ui.getCore().byId(this.EMPLOYEE_ORDERS_DETAILS_VIEW_ID).getModel().setProperty("/employee", userProfile.getData());

                splitApp.toMaster(this.EMPLOYEE_ORDERS_MASTER_VIEW_ID);
                splitApp.toDetail(sap.ui.getCore().byId(this.DEFAULT_DETAILS_VIEW_ID), "show");
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
