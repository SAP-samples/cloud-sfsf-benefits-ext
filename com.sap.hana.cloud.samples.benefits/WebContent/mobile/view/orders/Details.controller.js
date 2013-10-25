sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.orders.Details", {
    onInit: function() {
        this.busyDialog = new sap.m.BusyDialog({showCancelButton: false});

        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.getController().loadBenefitsModel();
                if (evt.data.context) {
                    this.setBindingContext(evt.data.context);
                    this.byId("addButton").setEnabled(evt.data.context.getObject().active);
                }
                var data = evt.data.additionalData.modelData;
                this.getController().initEmployeeDetailsModel(data.employee, data.campaignId);
            }
        }, this.getView());
    },
    initEmployeeDetailsModel: function(employeeProfile, campaignId) {
        this.employeeProfile = employeeProfile;
        this.campaignId = campaignId;
        if (!this.getView().getModel()) {
            this.getView().setModel(new sap.ui.model.json.JSONModel());
        }
        this.getView().getModel().setData({
            employee: employeeProfile
        });

        this.loadOrderDetails();
    },
    loadOrderDetails: function() {
        var orderDetails = jQuery.sap.syncGetJSON("../api/orders/for-user/" + this.campaignId + "/" + this.employeeProfile.userId).data;
        this.getView().getModel().setProperty("/currentOrder", orderDetails);
    },
    loadBenefitsModel: function() {
        if (!this.getView().getModel("benefitsModel")) {
            this.getView().setModel(new sap.ui.model.json.JSONModel(), "benefitsModel");
        }
        this.getView().getModel("benefitsModel").loadData("../api/benefits/all", null, false);
    },
    onAfterRendering: function() {
    },
    addItem: function() {
        jQuery.sap.require("sap.m.MessageToast");
        var dialog = this.byId("addItemCtrl");

        dialog.setLeftButton(new sap.m.Button({
            text: "Ok",
            press: jQuery.proxy(this._addItem, this)

        }));

        dialog.setRightButton(new sap.m.Button({
            text: "Cancel",
            press: function() {
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
    formatAvailablePoints: function(campaignPoints, usedPoints) {
        var result = campaignPoints - usedPoints;
        return result.toString(10) + " Points";
    },
    linkPressed: function(evt) {
        var sourceControl = evt.getSource();
        var model = sourceControl.getModel();
        var contextPath = sourceControl.getBindingContext().sPath + "/benefitDetails/infoLink";
        var link = model.getProperty(contextPath);
        sap.m.URLHelper.redirect(link, true);
    },
    onBenefitSelect: function(evt) {
        var ctx = evt.getParameters().selectedItem.getBindingContext("benefitsModel");
        this.byId("quantityTypeSelect").setBindingContext(ctx);
        this.byId("quantityTypeSelect").setModel(ctx.getModel());
    },
    handleDialogOpen: function(oCtrEvt) {
        var ctx = this.byId("benefitTypeSelect").getSelectedItem().getBindingContext("benefitsModel");
        this.byId("quantityTypeSelect").setBindingContext(ctx);
        this.byId("quantityTypeSelect").setModel(ctx.getModel());
    },
    onDelete: function(evt) {
        jQuery.sap.require("sap.m.MessageBox");
        var ctx = evt.getParameter("listItem").getBindingContext().getObject();
        var itemId = ctx.id;
        var itemDetails = ctx.name + " " + ctx.quantity + " x " + ctx.itemValue + " points";
        sap.m.MessageBox.confirm("Are you sure you want to delete order with details '" + itemDetails + "'?", jQuery.proxy(function(action) {
            if (action === sap.m.MessageBox.Action.OK) {
                this._deleteOrder(itemId);
            }
        }, this));
    },
    closeDateTimeSelectors: function() {
        this.byId("benefitTypeSelect").close();
        this.byId("quantityTypeSelect").close();
    },
    fireModelChange: function() {
        sap.ui.getCore().getEventBus().publish("refresh", "orders", {
            sourceId: this.getView().getId()
        });
    },
    _addItem: function(availablePoints) {
        jQuery.sap.require("sap.m.MessageBox");

        var currentOrder = this.getView().getModel().getProperty("/currentOrder");
        var campPoints = currentOrder.campaign.points;
        var orderPrice = currentOrder.orderPrice;
        var availablePoints = campPoints - orderPrice;
        var dialog = this.byId("addItemCtrl");
        var selItemVal = this.byId("quantityTypeSelect").getSelectedItem().getBindingContext().getObject().value;
        var quantity = this.byId("quantityTypeTxt").getValue();
        var value = selItemVal * quantity;
        var addServiceURL = "../api/orders/add/" + this.campaignId;
        var selectUserId = this.employeeProfile.userId;
        if (selectUserId) {
            addServiceURL += "/" + selectUserId;
        }

        if (value <= 0) {
            sap.m.MessageBox.alert("Insert correct value for the quantity", function() {
            });
        } else if (value <= availablePoints) {
            jQuery.ajax({
                url: addServiceURL,
                type: 'post',
                dataType: 'json',
                success: jQuery.proxy(function(data) {
                    dialog.close();
                    sap.m.MessageToast.show("New item has been saved");
                    this.fireModelChange();
                    this.loadOrderDetails();
                }, this),
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({campaignId: this.campaignId,
                    benefitTypeId: this.byId("quantityTypeSelect").getSelectedItem().getKey(),
                    quantity: this.byId("quantityTypeTxt").getValue()
                }),
                error: function(xhr, error) {
                    sap.m.MessageToast.show(xhr.responseText);
                }
            });
        } else {
            dialog.close();
            sap.m.MessageBox.alert("Item was not send, limit has been exceeded", function() {
            });
        }
    },
    _deleteOrder: function(orderId) {
        this.busyDialog.open();
        jQuery.ajax({
            url: '../api/orders/' + orderId,
            type: 'delete',
            success: jQuery.proxy(function(data) {
                this.fireModelChange();
                this.loadOrderDetails();
            }, this),
            complete: jQuery.proxy(function() {
                this.busyDialog.close();
            }, this)
        });
    }
});
