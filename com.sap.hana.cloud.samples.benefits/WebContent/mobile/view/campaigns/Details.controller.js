jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("sap.ui.core.format.DateFormat");
jQuery.sap.require("sap.m.MessageBox");
jQuery.sap.require("sap.m.MessageToast");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Details", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.setBindingContext(evt.data.context);
                this.setModel(evt.data.context.getModel());
                this.getController()._refreshStartStopBtnState();
            }
        }, this.getView());

        this.actionSheet = this._createActionSheet();
        this.eventBus = sap.ui.getCore().getEventBus();
    },
    onAfterRendering: function() {
    },
    onBeforeRendering: function() {
    },
    formatState: function(active) {
        return active ? sap.ui.core.ValueState.Success : sap.ui.core.ValueState.Error;
    },
    formatStateText: function(active) {
        return active ? "Active" : "Inactive";
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
    optionsBtnPressHandler: function(evt) {
        this.actionSheet.openBy(evt.getSource());
    },
    startStopButtonPressed: function(evt) {
        if (evt.getSource().state === 'stop') {
            this._requestStopCampaign();
        } else {
            this._startCampaign();
        }
    },
    changeDatesButtonPressed: function(evt) {
        var editCampDialog = this.byId("editCampaignDialog");
        editCampDialog.setLeftButton(new sap.m.Button({
            text: "Ok",
            press: jQuery.proxy(this._saveEditedDates, this)
        }));
        editCampDialog.setRightButton(new sap.m.Button({
            text: "Cancel",
            press: jQuery.proxy(function() {
                editCampDialog.close();
            }, this)
        }));

        this.byId("startDateCtr").setProperty('value', this.byId("startDateTextCtr").getText() === "not set" ? undefined : this.byId("startDateTextCtr").getText());
        this.byId("endDateCtr").setProperty('value', this.byId("endDateTextCtr").getText() === "not set" ? undefined : this.byId("endDateTextCtr").getText());
        this.eventBus.publish("nav", "virtual");
        editCampDialog.open();
    },
    fireModelChanged: function(action) {
        this.eventBus.publish("refresh", "campaigns", {
            sourceId: this.getView().getId(),
            action: action,
        });
    },
    _saveEditedDates: function(evt) {
        var startDate = this.byId("startDateCtr").getDateValue();
        var endDate = this.byId("endDateCtr").getDateValue();
        var isvalidPeriod = this._isValidDatePeriod(startDate, endDate);
        if (isvalidPeriod) {
            this.byId("editCampaignDialog").close();
            var ctx = this.byId("inputForm").getBindingContext().getObject();
            var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({style: "full", pattern: "yyyy-MM-dd'T'HH:mm:ss'Z'"});
            jQuery.ajax({
                url: '../api/campaigns/edit/' + ctx.id,
                type: 'post',
                dataType: 'json',
                success: jQuery.proxy(function(data) {
                    sap.m.MessageToast.show("Data Saved Successfully.");
                    this.fireModelChanged("edit");
                }, this),
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
            })
        } else {
            $(".errorContainer").removeClass("displayNone");
            if (!startDate || !endDate) {
                this.byId("errorStatusText").setText("You need to select a valid dates");
            } else {
                this.byId("errorStatusText").setText("The start date must be before end date.");
            }
        }
    },
    _isValidDatePeriod: function(startDate, endDate) {
        if (startDate && endDate && startDate.getTime() < endDate.getTime()) {
            return true;
        } else {
            return false;
        }
    },
    _deleteCampaignBtnPressed: function(evt) {
        var campaignId = this.byId("inputForm").getBindingContext().getObject().id;
        var campaignName = this.byId("inputForm").getBindingContext().getObject().name;
        sap.m.MessageBox.confirm("Are you sure you want to delete campaign '" + campaignName + "'?", jQuery.proxy(function(action) {
            if (action === sap.m.MessageBox.Action.OK) {
                this.getView().setBusy(true);
                jQuery.ajax({
                    url: '../api/campaigns/' + campaignId,
                    type: 'delete',
                    success: jQuery.proxy(function(data) {
                        this.fireModelChanged("delete");
                    }, this),
                    complete: jQuery.proxy(function() {
                        this.getView().setBusy(false);
                    }, this)
                });
            }
        }, this));
    },
    _refreshStartStopBtnState: function() {
        var isCampaignStarted = this.byId("inputForm").getBindingContext().getObject().active;
        if (isCampaignStarted) {
            this.startStopBtn.setText("Stop");
            this.startStopBtn.setIcon("sap-icon://decline");
            this.startStopBtn.state = 'stop';
        } else {
            this.startStopBtn.setText("Start");
            this.startStopBtn.setIcon("sap-icon://begin");
            this.startStopBtn.state = 'start';
        }
    },
    _startCampaign: function(evt) {
        if (this._validateCampaignDataExist()) {
            appController.setAppBusy(true);
            var ctx = this.byId("inputForm").getBindingContext().getObject();
            jQuery.ajax({
                url: '../api/campaigns/start-possible/' + ctx.id,
                type: 'get',
                dataType: 'json',
                success: jQuery.proxy(function(data) {
                    if (data.canBeStarted) {
                        this._requestStartCampaign();
                    } else {
                        this._showErrorMessageBox("Only one campaign can be active. Currently active campaign is \"" + data.startedCampaignName + "\"");
                    }
                }, this),
                complete: function() {
                    appController.setAppBusy(false);
                },
                contentType: "application/json; charset=utf-8",
            });
        } else {
            this._showErrorMessageBox("Unnable to start the campaign. Not all required fields are set!");
        }
    },
    _validateCampaignDataExist: function() {
        var campData = this.byId("inputForm").getBindingContext().getObject();
        if (campData.name && campData.startDate && campData.endDate && campData.points) {
            return true;
        }
        return false;
    },
    _requestStopCampaign: function() {
        var ctx = this.byId("inputForm").getBindingContext().getObject();
        jQuery.ajax({
            url: '../api/campaigns/stop/' + ctx.id,
            type: 'post',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                sap.m.MessageToast.show("Campaign Stoped");
                this.fireModelChanged("stop");
                this._refreshStartStopBtnState();
            }, this),
            contentType: "application/json; charset=utf-8"
        });
    },
    _requestStartCampaign: function() {
        var ctx = this.byId("inputForm").getBindingContext().getObject();
        jQuery.ajax({
            url: '../api/campaigns/start/' + ctx.id,
            type: 'post',
            dataType: 'json',
            success: jQuery.proxy(function(data) {
                sap.m.MessageToast.show("Campaign Started");
                this.fireModelChanged("start");
                this._refreshStartStopBtnState();
            }, this),
            contentType: "application/json; charset=utf-8"
        });
    },
    _createActionSheet: function() {
        this.startStopBtn = new sap.m.Button("startStopButton", {
            icon: "sap-icon://decline",
            text: "Stop",
            press: jQuery.proxy(this.startStopButtonPressed, this)
        });

        return new sap.m.ActionSheet({
            title: "Please choose one action",
            showCancelButton: true,
            buttons: [
                this.startStopBtn,
                new sap.m.Button({
                    icon: "sap-icon://edit",
                    text: "Change Dates",
                    press: jQuery.proxy(this.changeDatesButtonPressed, this)
                }),
                new sap.m.Button({
                    icon: "sap-icon://delete",
                    text: "Delete",
                    press: jQuery.proxy(this._deleteCampaignBtnPressed, this)
                })
            ],
            placement: sap.m.PlacementType.Top,
            afterClose: this._actionSheetAfterCloseEvtHandler
        });
    },
    _showErrorMessageBox: function(message) {
        sap.m.MessageBox.show(
                message,
                sap.m.MessageBox.Icon.ERROR,
                null,
                [sap.m.MessageBox.Action.OK]
                );
    },
    _actionSheetAfterCloseEvtHandler: function(evt) {
        if (evt.getParameter("origin")) {
            sap.ui.getCore().getEventBus().publish("nav", "back");
        }
    },
    _closeDateTimeSelectors: function() {
        this.byId("startDateCtr").close();
        this.byId("endDateCtr").close();
    }
});
