sap.ui.controller("com.sap.benefits.management.view.campaigns.Details", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.setBindingContext(evt.data.context);
                this.getController().refreshStartStopBtnState();
            }
        }, this.getView());

        this.busyDialog = new sap.m.BusyDialog({showCancelButton: false});
    },
    onAfterRendering: function() {
    },
    onBeforeRendering: function() {
    },
    editButtonPressed: function(evt) {
        var editCampDialog = this.byId("editCampaignDialog");
        editCampDialog.setLeftButton(new sap.m.Button({
            text: "Ok",
            press: jQuery.proxy(this.saveEditedCampaignData, this)
        }));
        editCampDialog.setRightButton(new sap.m.Button({
            text: "Cancel",
            press: jQuery.proxy(function() {
                editCampDialog.close();
            }, this)
        }));

        this.byId("startDateCtr").setValue(this.byId("startDateTextCtr").getText());
        this.byId("endDateCtr").setValue(this.byId("endDateTextCtr").getText());
        sap.ui.getCore().getEventBus().publish("nav", "virtual");
        editCampDialog.open();
    },
    saveEditedCampaignData: function(evt) {
        jQuery.sap.require("sap.m.MessageToast");
        jQuery.sap.require("sap.ui.core.format.DateFormat");
        this.byId("editCampaignDialog").close();
        var ctx = this.byId("inputForm").getBindingContext().getObject();
        var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({style: "full", pattern: "yyyy-MM-dd'T'HH:mm:ss'Z'"});
        jQuery.ajax({
            url: '/com.sap.benefits.management/api/campaigns/admin/' + ctx.id,
            type: 'post',
            dataType: 'json',
            success: function(data) {
                sap.m.MessageToast.show("Data Saved Successfully.");
                appController.reloadCampaignModel();
            },
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                id: ctx.id,
                startDate: dateFormat.format(this.byId("startDateCtr").getDateValue()),
                endDate: dateFormat.format(this.byId("endDateCtr").getDateValue())
            }),
            statusCode: {
                400: function(xhr, error) {
                    sap.m.MessageToast.show(xhr.responseText);
                }
            }
        });
    },
            startStopButtonPressed: function(evt) {
        if (evt.getSource().state === 'stop') {
            this._requestStopCampaign();
        } else {
            this.startCampaign();
        }
    },
    startCampaign: function(evt) {
        jQuery.sap.require("sap.m.MessageBox");
        jQuery.sap.require("sap.m.MessageToast");
        this.busyDialog.open();
        var ctx = this.byId("inputForm").getBindingContext().getObject();
        jQuery.ajax({
            url: '/com.sap.benefits.management/api/campaigns/admin/start/' + ctx.id,
            type: 'get',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                this.busyDialog.close();
                if (data.canBeStarted) {
                    this._requestStartCampaign();
                } else {
                    sap.m.MessageBox.alert("Cannot start campaign. Reason: Only one campaign at time can be started. Currently started campaign :\"" + data.startedCampaignName + "\"");
                }
            }, this),
            contentType: "application/json; charset=utf-8",
        });
    },
    setState: function(active) {
        jQuery.sap.require("sap.ui.core.ValueState");
        if (active) {
            return sap.ui.core.ValueState.Success;
        } else {
            return sap.ui.core.ValueState.Error;
        }
    },
    setStateText: function(active) {
        if (active) {
            return "Active";
        } else {
            return "Inactive";
        }
    },
    setStartStopButtonText: function(active) {
        return active ? "Stop" : "Start";
    },
    refreshStartStopBtnState: function(isCampaignStarted) {
        var isCampaignStarted = this.getView().getBindingContext().getObject().active;
        if (isCampaignStarted) {
            this.byId("startStopButton").setText("Stop");
            this.byId("startStopButton").state = 'stop';
        } else {
            this.byId("startStopButton").setText("Start");
            this.byId("startStopButton").state = 'start';
        }
    },
    _requestStopCampaign: function() {
        jQuery.sap.require("sap.m.MessageToast");
        var ctx = this.byId("inputForm").getBindingContext().getObject();
        jQuery.ajax({
            url: '/com.sap.benefits.management/api/campaigns/admin/stop/' + ctx.id,
            type: 'post',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                sap.m.MessageToast.show("Campaign Stoped");
                appController.reloadCampaignModel();
                this.refreshStartStopBtnState();
            }, this),
            contentType: "application/json; charset=utf-8"
        });
    },
    _requestStartCampaign: function() {
        jQuery.sap.require("sap.m.MessageToast");
        var ctx = this.byId("inputForm").getBindingContext().getObject();
        jQuery.ajax({
            url: '/com.sap.benefits.management/api/campaigns/admin/start/' + ctx.id,
            type: 'post',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                sap.m.MessageToast.show("Campaign Started");
                appController.reloadCampaignModel();
                this.refreshStartStopBtnState();
            }, this),
            contentType: "application/json; charset=utf-8"
        });
    }

});
