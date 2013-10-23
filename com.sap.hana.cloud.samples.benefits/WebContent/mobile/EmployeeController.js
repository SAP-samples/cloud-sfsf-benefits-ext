jQuery.sap.declare("EmployeeController");
jQuery.sap.require("sap.ui.app.Application");

sap.ui.app.Application.extend("Application", {
    init: function() {

    	campId = "";
        var campaignModel = new sap.ui.model.json.JSONModel();

        sap.ui.getCore().setModel(campaignModel, "campaignModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
        
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeOrderDetailsModel");
        sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel(), "employeeDetailsModel");
        // Loads the logged in user profile
        sap.ui.getCore().getModel("employeeDetailsModel").loadData("../api/user/profile", null, false);
        sap.ui.getCore().getModel("employeeOrderDetailsModel").setProperty("/employee",sap.ui.getCore().getModel("employeeDetailsModel").getData());
        
        this.reloadCampaignModel();
        this.reloadBenefitsModel();
    },
    reloadCampaignModel: function() {
        sap.ui.getCore().getModel("campaignModel").loadData("/com.sap.hana.cloud.samples.benefits/api/user/campaigns", null, false);
    },    
    reloadBenefitsModel : function() {
        sap.ui.getCore().getModel("benefitsModel").loadData("/com.sap.hana.cloud.samples.benefits/api/benefits/all", null, false);
    },
    reloadOrdersModel : function(campaignId){
    	if (campaignId) {
            jQuery.ajax({
                url : "../api/user/orders/" + campaignId,
                type : 'GET',
                success : function(data) {
                    sap.ui.getCore().getModel("employeeOrderDetailsModel").setProperty("/currentOrder", data);
                }
            });            
        }    	
    },
    getCampaignId : function(){
    	return campId;
    },
    getSelecedUserID : function() {
        // Relevant only for HR Manager role.
        return undefined;
    },

    campaignItemSelected: function(evt) {
    	var listItem = evt.getParameters().listItem;
    	var bindingCtx = listItem.getBindingContext("campaignModel");
    	campId = bindingCtx.getObject().id;
    	this.reloadOrdersModel(campId);
    	this._toDetailsPage("EmployeeOrdersDetails", {
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
//                    number: "12",
                    title: "Orders",
                    press: jQuery.proxy(this._handleTilePressed, this)
                }),
            ]
        });

        var emplOrdersMasterView =  sap.ui.xmlview("EmployeeOrdersMaster", "com.sap.hana.cloud.samples.benefits.view.orders.Master");
        var emplOrdersDetailsView =  sap.ui.xmlview("EmployeeOrdersDetails", "com.sap.hana.cloud.samples.benefits.view.orders.Details");
        emplOrdersDetailsView.setModel(sap.ui.getCore().getModel("employeeOrderDetailsModel"));
        
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

});
