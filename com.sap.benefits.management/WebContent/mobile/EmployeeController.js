jQuery.sap.declare("EmployeeController");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    init: function() {
        var employeesModel = new sap.ui.model.json.JSONModel();
        employeesModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testData.json", null, false);
        
        var ordersModel = new sap.ui.model.json.JSONModel();
        ordersModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/employeeOrders.json", null, false);                

        var campaignModel = new sap.ui.model.json.JSONModel();
        campaignModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testDataCampaigns.json", null, false);

        sap.ui.getCore().setModel(ordersModel, "ordersModel");
        sap.ui.getCore().setModel(employeesModel, "employeesModel");
        sap.ui.getCore().setModel(campaignModel, "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignDetailsModel");
    },
    employeeItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("employeesModel");

        sap.ui.getCore().byId("EmployeeOrdersDetails").byId("EmployeeOrdersPage").setTitle(bindingCtx.getObject().id + " Details");
        sap.ui.getCore().getModel("employeeDetailsModel")
                .setData({
            current: bindingCtx.getObject().orders.current,
            history: bindingCtx.getObject().orders.history});

        this._toDetailsPage("EmployeesDetails");
    },
    campaignItemSelected: function(evt) {
    	var listItem = evt.getParameters().listItem;
    	var bindingCtx = listItem.getBindingContext("campaignModel");
    	var employeeCtx = sap.ui.getCore().getModel("employeesModel").getData().employees[0];
    	sap.ui.getCore().byId("EmployeeOrdersDetails").byId("EmployeeOrdersPage").setTitle(bindingCtx.getObject().name + " Details");
    	sap.ui.getCore().getModel("employeeDetailsModel").setData({
		    current: employeeCtx.orders.current});
    	this._toDetailsPage("EmployeeOrdersDetails");
    },
    selectListItem: function(list, itemIndex) {
        var items = list.getItems();
        if (items[itemIndex]) {
            items[itemIndex].setSelected(true);
            list.fireSelect({listItem: items[itemIndex], id: list.getId()});
        }
    },
    goHome: function() {
        var homePage = sap.ui.getCore().byId("HomePage");
        this._getShell().setApp(homePage);
    },
    main: function() {
        var root = this.getRoot();

        var tileContainer = new sap.m.TileContainer("HomePage", {
            tiles: [               
                new sap.m.StandardTile("Orders", {
                    icon: "sap-icon://new-order",
                    number: "12",
                    title: "Orders",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }),
            ]
        });

        var emplOrdersMasterView =  sap.ui.xmlview("EmployeeOrdersMaster", "com.sap.benefits.management.view.orders.Master");
        var emplOrdersDetailsView =  sap.ui.xmlview("EmployeeOrdersDetails", "com.sap.benefits.management.view.orders.Details");
        
        var splitApp = new sap.m.SplitApp("SplitAppControl");
  
        splitApp.addMasterPage(emplOrdersMasterView);
        splitApp.addDetailPage(emplOrdersDetailsView);

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
        	splitApp.toMaster("EmployeeOrdersMaster");
            splitApp.toDetail(sap.ui.getCore().byId("DefaultDetails"), "show");
            this._getShell().setApp(splitApp);        
    },
    
    _toDetailsPage: function(pageId) {
        var splitApp = sap.ui.getCore().byId("SplitAppControl");
        splitApp.toDetail(sap.ui.getCore().byId(pageId), "show");
    }

});
