jQuery.sap.declare("EmployeeController");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    init: function() {

    	campId = "";
        var campaignModel = new sap.ui.model.json.JSONModel();
        var ordersModel = new sap.ui.model.json.JSONModel();

        sap.ui.getCore().setModel(ordersModel, "ordersModel");
        sap.ui.getCore().setModel(campaignModel, "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "orderDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "campaignDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "orderByCampaignModel");
        
        this.reloadCampaignModel();
        this.reloadBenefitsModel();
    },
    reloadCampaignModel: function() {
        sap.ui.getCore().getModel("campaignModel").loadData("/com.sap.benefits.management/api/user/userCampaigns", null, false);
    },
//    reloadOrdersModel : function(userId){
//    	sap.ui.getCore().getModel("ordersModel").loadData("/com.sap.benefits.management/api/orders/ordersForUser/" + userId, null, false);
//    },
    
    reloadBenefitsModel : function() {
        sap.ui.getCore().getModel("benefitsModel").loadData("/com.sap.benefits.management/api/benefits/all", null, false);
        var testCtx = sap.ui.getCore().getModel("benefitsModel").getData();
    },
    reloadOrdersModel : function(campaignId){
    	var model = new sap.ui.model.json.JSONModel();
    	if (campaignId) {
            jQuery.ajax({
                url : "../api/user/orders/" + campaignId,
                type : 'GET',
                success : function(data) {
                    model.setProperty("/currentOrder", data);
                }
            });
        }
    	sap.ui.getCore().getModel("orderByCampaignModel").setData(model);
    	sap.ui.getCore().byId("EmployeeOrdersDetails").setModel(model);
    },
    getCampaignId : function(){
    	return campId;
    },

    campaignItemSelected: function(evt) {
    	var listItem = evt.getParameters().listItem;
    	var bindingCtx = listItem.getBindingContext("campaignModel");
    	campId = bindingCtx.getObject().id;
    	this.reloadOrdersModel(campId);
    	sap.ui.getCore().byId("EmployeeOrdersDetails").byId("EmployeeOrdersPage").setTitle(bindingCtx.getObject().name + " Details");
    	var testCtx = sap.ui.getCore().getModel("orderByCampaignModel").getData();
    	this._toDetailsPage("EmployeeOrdersDetails", {
    		isActive : bindingCtx.getObject().active
    	});
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
    	
    	var transformedOrders = this._transformOrdersData(ordersForSelectedCamp);
    	return transformedOrders;
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
    				frontEndOrders.employee.orders[j].benefitTypeId = roughOrders[i].orderDetails[j].benefitType.id;
    				frontEndOrders.employee.orders[j].benefitTypeName = roughOrders[i].orderDetails[j].benefitType.name;
    				frontEndOrders.employee.orders[j].quantity = roughOrders[i].orderDetails[j].quantity;
    				frontEndOrders.employee.orders[j].value = roughOrders[i].orderDetails[j].benefitType.value;
    			}    			
    		}
    	}
    	return frontEndOrders;
    }

});
