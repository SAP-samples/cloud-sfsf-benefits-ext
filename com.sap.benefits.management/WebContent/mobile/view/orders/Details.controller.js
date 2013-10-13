sap.ui.controller("com.sap.benefits.management.view.orders.Details", {
	monthNames : [ "January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December" ],
	onInit : function() {
		this.byId("addItemCtrl").addEventDelegate({
            onInit : function() {
                
            	this.setLeftButton(new sap.m.Button({
    				text : "Ok",
    				press : function() {
    					dialog.close();
    				}
    			}));
    			this.setRightButton(new sap.m.Button({
    				text : "Cancel",
    				press : function() {
    					dialog.close();
    				}
    			}));			
    		}
            
        }, this.byId("addItemCtrl"));

	},
	onAfterRendering : function() {
	},
	formatTypes : function(types) {
		var text = "";
		for (type in types) {
			text += types[type].quantity + 'x' + types[type].name + ", ";
		}
		return text.slice(0, -2);
	},
	formatOrders : function(orders) {
		var text = "";
		for (order in orders) {
			text += orders[order].quantity + 'x' + orders[order].type;
		}
		return text;
	},
	usedPoints : function(availableP, totalP){
		return "Used points " + (totalP - availableP);
	},	
	
	groupBenefits : function(oContext) {
		var date = new Date(oContext.getProperty("date"));
		return {
			key : date.getMonth() + 1,
			text : this.monthNames[date.getMonth()]
		};
	},

	itemGrouping : function(oContext) {
		var benefitTypeId = oContext.getProperty("id");
		return {
			key : benefitTypeId,
			text : benefitTypeId//oContext.getProperty("item")
		};
	},

	wholeValue : function(quantity, type) {
		if(type && quantity){
			var typeValue = type.split(" ")[0];
			var typeCurr = type.split(" ")[1];
			return "total value: " + quantity * typeValue + " " + typeCurr;	
		}
		return "";
	},
	
	addItem : function() {
		jQuery.sap.require("sap.m.MessageToast");
		

		var dialog = this.byId("addItemCtrl");
		
		dialog.setLeftButton(new sap.m.Button({
			text : "Ok",
			press : function() {
				dialog.close();
				sap.m.MessageToast.show("New item has been saved");
			}
		}));
		
		dialog.setRightButton(new sap.m.Button({
			text : "Cancel",
			press : function() {
				dialog.close();
				sap.m.MessageToast.show("Item was canceled");
			}
		}));	
		
		dialog.open();
	}

});
