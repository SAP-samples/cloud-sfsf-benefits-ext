sap.ui.controller("com.sap.benefits.management.view.orders.Details", {
	
	onInit : function() {
		this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
            	this.setBindingContext(evt.data.context);                		
            	if(evt.data.context){
            		this.byId("addButton").setEnabled(evt.data.context.getObject().active);            		
            	}
            }
        }, this.getView());
	},
	
	onAfterRendering : function() {
	},
	
	addItem : function() {
		jQuery.sap.require("sap.m.MessageToast");
		var dialog = this.byId("addItemCtrl");
		var that = this;
		
		dialog.setLeftButton(new sap.m.Button({
			text : "Ok",
			press : function() {			
				jQuery.ajax({
		            url: '/com.sap.benefits.management/api/orders/add/' + appController.getCampaignId(),
		            type: 'post',
		            dataType: 'json',
		            success: function(data) {          
		            	dialog.close();
						sap.m.MessageToast.show("New item has been saved");
		                appController.reloadOrdersModel(appController.getCampaignId());
		            },
		            contentType: "application/json; charset=utf-8",
		            data: JSON.stringify({campaignId: appController.getCampaignId(),
		            					  benefitTypeId : that.byId("quantityTypeSelect").getSelectedItem().getKey(),
		            					  quantity : that.byId("quantityTypeTxt").getValue()
		            }),
		            error: function(xhr, error) {
		                sap.m.MessageToast.show(xhr.responseText);
		            }
		        });
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
	},
	
    formatBenefitItemsSum: function(benefitItems) {
        var result = 0;
        for (benefitItem in benefitItems) {
            result += benefitItems[benefitItem].quantity * benefitItems[benefitItem].itemValue;
        }
        return result;
    },
    formatAvailablePoints: function(campaignPoints,usedPoints) {
        var result = campaignPoints - usedPoints;     
        return result.toString(10) + " Points";
    },
    linkPressed : function(evt){
        var sourceControl = evt.getSource();
        var model = sourceControl.getModel();
        var contextPath = sourceControl.getBindingContext().sPath+"/benefitDetails/infoLink";
        var link = model.getProperty(contextPath);
        sap.m.URLHelper.redirect(link, true);
    },
    onBenefitSelect : function(evt){
    	var ctx = evt.getParameters().selectedItem.getBindingContext("benefitsModel");
    	this.byId("quantityTypeSelect").setBindingContext(ctx);
    	this.byId("quantityTypeSelect").setModel(ctx.getModel());
    },
    handleDialogOpen : function(oCtrEvt){
    	var ctx = this.byId("benefitTypeSelect").getSelectedItem().getBindingContext("benefitsModel");
    	this.byId("quantityTypeSelect").setBindingContext(ctx);
    	this.byId("quantityTypeSelect").setModel(ctx.getModel());
    }

});
