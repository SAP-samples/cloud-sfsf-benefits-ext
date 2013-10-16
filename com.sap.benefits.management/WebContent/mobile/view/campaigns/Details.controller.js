jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("sap.ui.core.format.DateFormat");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Details", {
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

        this.byId("startDateCtr").setValue(this.byId("startDateTextCtr").getText() === "not set" ? "" : this.byId("startDateTextCtr").getText());
        this.byId("endDateCtr").setValue(this.byId("endDateTextCtr").getText() === "not set" ? "" : this.byId("endDateTextCtr").getText());
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
            url: '../api/campaigns/edit/' + ctx.id,
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
            url: '../api/campaigns/start-possible/' + ctx.id,
            type: 'get',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                if (data.canBeStarted) {
                    this._requestStartCampaign();
                } else {
                    sap.m.MessageBox.alert("Only one campaign can be active. Currently active campaign is \"" + data.startedCampaignName + "\"");
                }
            }, this),
            complete: jQuery.proxy(function() {
                this.busyDialog.close();
            }, this),
            contentType: "application/json; charset=utf-8",
        });
    },
    formatState: function(active) {
        return active ? sap.ui.core.ValueState.Success : sap.ui.core.ValueState.Error;
    },
    formatStateText: function(active) {
        return active ? "Active" : "Inactive";
    },
    formatStartStopButtonText: function(active) {
        return active ? "Stop" : "Start";
    },
    formatDate: function(date) {
        if (date) {
            var formatter = sap.ui.core.format.DateFormat;
            var dateObject = formatter.getDateInstance({style: "full", pattern: "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'"}).parse(date);
            return formatter.getDateInstance({style: "full", pattern: "MMM d, y"}).format(dateObject);
        } else {
            return "not set";
        }
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
            url: '../api/campaigns/stop/' + ctx.id,
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
            url: '../api/campaigns/start/' + ctx.id,
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
