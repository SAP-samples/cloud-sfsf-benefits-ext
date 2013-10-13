jQuery.sap.declare("EmployeeController");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    init: function() {
        var employeesModel = new sap.ui.model.json.JSONModel();
        employeesModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testData.json", null, false);
        
//        ordersModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/employeeOrders.json", null, false);    
        
        var benefitsModel = new sap.ui.model.json.JSONModel();
        benefitsModel.loadData(jQuery.sap.getModulePath("com.sap.benefits.management") + "/model/testDataBenefits.json", null, false);

        var campaignModel = new sap.ui.model.json.JSONModel();
        var ordersModel = new sap.ui.model.json.JSONModel();

        sap.ui.getCore().setModel(ordersModel, "ordersModel");
        sap.ui.getCore().setModel(employeesModel, "employeesModel");
        sap.ui.getCore().setModel(benefitsModel, "benefitsModel");
        sap.ui.getCore().setModel(campaignModel, "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "orderDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignDetailsModel");
        
        this.reloadCampaignModel();
        this.reloadOrdersModel("cgrant1");//userId
    },
    reloadCampaignModel: function() {
        sap.ui.getCore().getModel("campaignModel").loadData("/com.sap.benefits.management/api/user/userCampaigns", null, false);
    },
    reloadOrdersModel : function(userId){
    	sap.ui.getCore().getModel("ordersModel").loadData("/com.sap.benefits.management/api/orders/ordersForUser/" + userId, null, false);
    },
    employeeItemSelected: function(evt) {
        var listItem = evt.getParameters().listItem;
        var bindingCtx = listItem.getBindingContext("employeesModel");

        sap.ui.getCore().byId("EmployeeOrdersDetails").byId("EmployeeOrdersPage").setTitle(bindingCtx.getObject().id + " Details");
        sap.ui.getCore().getModel("employeeDetailsModel")
                .setData({
            current: bindingCtx.getObject().orders.current,
            history: bindingCtx.getObject().orders.history});

        this._toDetailsPage("EmployeeOrdersDetails");
    },
    campaignItemSelected: function(evt) {
    	var listItem = evt.getParameters().listItem;
    	var bindingCtx = listItem.getBindingContext("campaignModel");
    	var allOrdersData = sap.ui.getCore().getModel("ordersModel").getData();
    	var ordersForCamp = this._getOrdersModelForCampaign(allOrdersData, bindingCtx.getObject().id);
    	var frontEndOrders = this._transformOrdersData(ordersForCamp);
    	sap.ui.getCore().byId("EmployeeOrdersDetails").byId("EmployeeOrdersPage").setTitle(bindingCtx.getObject().name + " Details");
    	sap.ui.getCore().getModel("orderDetailsModel").setData(frontEndOrders); //{current: frontEndOrders}
    	var testCtx = sap.ui.getCore().getModel("orderDetailsModel").getData();
    	this._toDetailsPage("EmployeeOrdersDetails");
    },
    
    _getOrdersModelForCampaign : function(allOrders, campaignId){
    	var ordersForSelectedCamp = [];
    	for(var i = 0; i < allOrders.length; i++){
    		if(allOrders[i].campaign){
    			if(allOrders[i].campaign.id == campaignId){
    				ordersForSelectedCamp.push(allOrders[i]);
    			}
    		}
    	}
    	
    	return ordersForSelectedCamp;
    },
    
//    campaignItemSelected : function(evt) {
//        var listItem = evt.getParameters().listItem;
//        var bindingCtx = listItem.getBindingContext("campaignModel");
//        sap.ui.getCore().byId("CampaignDetails").byId("inputForm").setModel(bindingCtx.getModel());
//        this._toDetailsPage("DefaultDetails");
//        this._toDetailsPage("CampaignDetails", {
//            context : bindingCtx
//        });
//    },
    
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
    },
    _transformOrdersData: function(roughOrders){
    	var frontEndOrders = new Object();
    	frontEndOrders.employee = new Object();
    	frontEndOrders.employee.orders = [];
    	
    	for(var i = 0; i < roughOrders.length; i++){
    		if(roughOrders[i].orderDetails && roughOrders[i].campaign){
    			if(!frontEndOrders.employee.campId){
    				frontEndOrders.employee.campId = roughOrders[i].campaign.id;
    				frontEndOrders.employee.campName = roughOrders[i].campaign.name;
    				frontEndOrders.employee.campPoints = roughOrders[i].campaign.points;
    			}
    			for(var j=0; j< roughOrders[i].orderDetails.length; j++){    				
    				frontEndOrders.employee.orders.push(new Object());
    				frontEndOrders.employee.orders[j].itemId = roughOrders[i].orderDetails[j].benefitType.id;
    				frontEndOrders.employee.orders[j].type = roughOrders[i].orderDetails[j].benefitType.name;
    				frontEndOrders.employee.orders[j].quantity = roughOrders[i].orderDetails[j].quantity;
    				frontEndOrders.employee.orders[j].value = roughOrders[i].orderDetails[j].benefitType.value;
    			}    			
    		}
    	}
    	return frontEndOrders;
    }

});
