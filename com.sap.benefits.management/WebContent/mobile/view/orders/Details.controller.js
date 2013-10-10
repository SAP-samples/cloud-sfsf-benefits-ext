sap.ui.controller("com.sap.benefits.management.view.orders.Details", {
    monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
    onInit: function() {

    },
    onAfterRendering: function() {
    },
    formatTypes: function(types) {
        var text = "";
        for (type in types) {
            text += types[type].quantity + 'x' + types[type].name + ", ";
        }
        return text.slice(0, -2);
    },
    formatOrders: function(orders){
    	var text = "";
        for (order in orders) {
            text += orders[order].quantity + 'x' + orders[order].type;
        }
        return text;
    },
    groupBenefits: function(oContext) {
        var date = new Date(oContext.getProperty("date"));
        return {
            key: date.getMonth() + 1,
            text: this.monthNames[date.getMonth()]
        }
    },
    
    itemGrouping: function(oContext) {
    	var itemId = oContext.getProperty("itemId");
    	return {
            key: itemId,
            text: oContext.getProperty("item")
        }
    },
    
    wholeValue : function(oContext) {
    	return "Whole Quantity in levs";
    },
    
    addItem : function () {
//        popover.close();
       jQuery.sap.require("sap.m.MessageToast");
       sap.m.MessageToast.show("EMail has been send");
    }
    
});
