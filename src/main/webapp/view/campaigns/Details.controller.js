jQuery.sap.require("com.sap.hana.cloud.samples.benefits.util.AjaxUtil");
jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("sap.ui.core.format.DateFormat");
jQuery.sap.require("sap.m.MessageBox");
jQuery.sap.require("sap.m.MessageToast");

sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Details", {
	onInit : function() {
		sap.ui.getCore().getEventBus().subscribe("app", "campaignDetailsRefresh", this._refreshHandler, this);
		formatter = sap.ui.core.format.DateFormat;
		this.getView().addEventDelegate({
			onBeforeShow : function(evt) {

				if (evt.data && evt.data.context) {
					this.setBindingContext(evt.data.context);
					this.setModel(evt.data.context.getModel());
					this.getController()._refreshStartStopBtnState();
				}

			}
		}, this.getView());
	},

	onBeforeRendering : function() {
		this.hideLogout();
	},

	formatStateText : function(active) {
		var stateControl = this.byId("stateCtr");
		stateControl.removeStyleClass("green").removeStyleClass("red");
		stateControl.addStyleClass(active ? "green" : "red");
		return this.getModelProperty(active ? "CAMPAIGN_STATUS_ACTIVE" : "CAMPAIGN_STATUS_INACTIVE");
	},

	getDateObject : function(date, format) {
		var parsedDate = new Date(parseInt(date.substr(6)));
		var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
			pattern : "dd/MM/yyyy"
		});
		var TZOffsetMs = new Date(0).getTimezoneOffset() * 60 * 1000;
		var dateStr = dateFormat.format(new Date(parsedDate.getTime() + TZOffsetMs));
		var dateObject = formatter.getDateInstance({
			style : "full",
			pattern : "dd/MM/yyyy"
		}).parse(dateStr);
		return formatter.getDateInstance({
			style : "full",
			pattern : format
		}).format(dateObject);
	},

	formatDate : function(date) {
		if (date) {
			return this.getDateObject(date, "MMM d, y");
		} else {
			var notSetMsg = this.getModelProperty("NOT_SET_MSG");
			return notSetMsg;
		}
	},

	formatDateTxt : function(date) {
		if (date) {
			return this.getDateObject(date, "dd.MM.yy");
		} else {
			return undefined;
		}
	},

	startStopButtonPressed : function(evt) {
		evt.getSource().state === 'stop' ? this._requestStopCampaign() : this._startCampaign();
	},

	changeDatesButtonPressed : function(evt) {
		if (!this.editCampaignDialog) {
			this.editCampaignDialog = sap.ui.xmlfragment("editCampaignDialog", "view.campaigns.editCampaignDialog", this);
			this._attachSelectionOnKeyDown("editCampaignDialog--startDateCtr");
			this._attachSelectionOnKeyDown("editCampaignDialog--endDateCtr");
		}
		var startDate = this.byId("inputForm").getBindingContext().getObject().StartDate;
		var endDate = this.byId("inputForm").getBindingContext().getObject().EndDate;
		sap.ui.getCore().byId("editCampaignDialog--startDateCtr").setValue(this.formatDateTxt(startDate));
		sap.ui.getCore().byId("editCampaignDialog--endDateCtr").setValue(this.formatDateTxt(endDate));
		sap.ui.getCore().getEventBus().publish("nav", "virtual");
		this.editCampaignDialog.open();
	},

	cancelButtonPressed : function() {
		this.editCampaignDialog.close();
	},

	_attachSelectionOnKeyDown : function(sId) {
		var input = sap.ui.getCore().byId(sId);
		input.attachBrowserEvent("keydown", function(e) {
			if (e.keyCode === 40) {
				input.onsapshow(e);
			}
		});
	},

	fireModelChanged : function(action) {
		sap.ui.getCore().getEventBus().publish("refresh", "campaigns", {
			sourceId : this.getView().getId(),
			action : action
		});
	},

	closeDateTimeSelectors : function() {
		sap.ui.getCore().byId("editCampaignDialog--startDateCtr").close();
		sap.ui.getCore().byId("editCampaignDialog--endDateCtr").close();
		sap.ui.getCore().byId("editCampaignDialog--startDateCtr").focus();
	},

	hideLogout : function() {
		this.byId("logoutButton").setVisible(appController._hasLogoutButton());
	},

	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	},

	getModelProperty : function(msgId) {
		var message = sap.ui.getCore().getModel("b_i18n").getProperty(msgId);
		return message;
	},

	_refreshHandler : function(channelId, eventId, data) {
		this.getView().setBindingContext(data.context);
		this.getView().setModel(data.context.getModel());
		this._refreshStartStopBtnState();
	},

	_saveEditedDates : function(evt) {
		var startDate = sap.ui.getCore().byId("editCampaignDialog--startDateCtr").getDateValue();
		var endDate = sap.ui.getCore().byId("editCampaignDialog--endDateCtr").getDateValue();
		var isValidPeriod = this._isValidDatePeriod(startDate, endDate);
		if (isValidPeriod) {
			this.editCampaignDialog.close();

			var doneCallback = function() {
				sap.m.MessageToast.show(this.getModelProperty("DATA_SAVED_MSG"));
				this.fireModelChanged("edit");
			};

			var failCallback = function() {
				this._showErrorMessageBox(this.getModelProperty("CAMPAIGN_DATE_EDIT_FAILED"));
			};

			var alwaysCallback = function() {
				appController.setAppBusy(false);
			};
			appController.setAppBusy(true);

			com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchPostJSON(this, this._createSaveRequestPath(), null,
					doneCallback, failCallback, alwaysCallback);
		} else {
			$(".errorContainer").removeClass("displayNone");
			var message = this.getModelProperty(!startDate || !endDate ? "INVALID_DATES_MSG" : "INVALID_PERIOD_MSG");
			sap.ui.getCore().byId("editCampaignDialog--errorStatusText").setText(message);
		}
	},

	_isValidDatePeriod : function(startDate, endDate) {
		return startDate && endDate && startDate.getTime() < endDate.getTime();
	},

	_createSaveRequestPath : function() {
		var ctx = this.byId("inputForm").getBindingContext().getObject();

		var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
			pattern : "yyyy-MM-dd'T'HH:mm:ss"
		});
		var startDate = dateFormat.format(sap.ui.getCore().byId("editCampaignDialog--startDateCtr").getDateValue());
		var endDate = dateFormat.format(sap.ui.getCore().byId("editCampaignDialog--endDateCtr").getDateValue());
		return 'OData.svc/editCampaign?startDate=datetime\'' + startDate + '\'&endDate=datetime\'' + endDate
				+ '\'&campaignid=' + ctx.Id;
	},

	_deleteCampaignBtnPressed : function(evt) {
		var campaignId = this.byId("inputForm").getBindingContext().getObject().Id;
		var campaignName = this.byId("inputForm").getBindingContext().getObject().Name;
		var message = sap.ui.getCore().getModel("b_i18n").getProperty("DELETE_CAMPAIGN_MSG").formatPropertyMessage(
				campaignName);
		sap.m.MessageBox.confirm(message, jQuery.proxy(function(action) {
			if (action !== sap.m.MessageBox.Action.OK) {
				return;
			}

			var doneCallback = function() {
				this.fireModelChanged("delete");
			};

			var failCallback = function() {
				this._showErrorMessageBox(this.getModelProperty("CAMPAIGN_DELETION_FAILD"));
			};

			var alwaysCallback = function() {
				appController.setAppBusy(false);
			};
			appController.setAppBusy(true);

			var requestPath = 'OData.svc/deleteCampaign?campaignId=' + campaignId;
			com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchDelete(this, requestPath, doneCallback, failCallback,
					alwaysCallback);
		}, this));
	},

	_refreshStartStopBtnState : function() {
		var startStopBtn = this.byId("startStopBtn");
		var isCampaignStarted = this.getView().getBindingContext().getObject().Active;
		if (isCampaignStarted) {
			startStopBtn.setText(this.getModelProperty("STOP_BTN_NAME"));
			startStopBtn.setIcon("sap-icon://decline");
			startStopBtn.state = 'stop';
		} else {
			startStopBtn.setText(this.getModelProperty("START_BTN_NAME"));
			startStopBtn.setIcon("sap-icon://begin");
			startStopBtn.state = 'start';
		}
	},

	_startCampaign : function(evt) {
		if (!this._validateCampaignDataExist()) {
			this._showErrorMessageBox(this.getModelProperty("UNABLE_START_CAMPAIGN_MSG"));
			return;
		}
		var ctx = this.byId("inputForm").getBindingContext().getObject();
		if (this._hasAlreadyStaredCampaign()) {
			this._showErrorMessageBox(this.getModelProperty("UNABLE_START_MORE_CAMPAIGNS_MSG"));
		} else {
			this._requestStartCampaign(ctx.Id);
		}
	},

	_requestStartCampaign : function(sStartCampaignId) {
		var doneCallback = function() {
			sap.m.MessageToast.show(this.getModelProperty("STARTED_CAMPAIGN_MSG"));
			this.fireModelChanged("start");
			this._refreshStartStopBtnState();
		};

		var failCallback = function() {
			this._showErrorMessageBox(this.getModelProperty("CAMPAIGN_START_FAILED"));
		};

		var alwaysCallback = function() {
			appController.setAppBusy(false);
		};

		appController.setAppBusy(true);

		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchPostJSON(this, 'OData.svc/startCampaign?campaignId='
				+ sStartCampaignId, null, doneCallback, failCallback, alwaysCallback);
	},

	_validateCampaignDataExist : function() {
		var campData = this.byId("inputForm").getBindingContext().getObject();
		return campData.Name && campData.StartDate && campData.EndDate;
	},

	_hasAlreadyStaredCampaign : function() {
		var aCampaigns = this.getView().getModel().getData().d.results;

		return aCampaigns.some(function(campaign) {
			return campaign.Active === true;
		});
	},

	_requestStopCampaign : function() {
		var doneCallback = function() {
			sap.m.MessageToast.show(this.getModelProperty("STOPPED_CAMPAIGN_MSG"));
			this.fireModelChanged("stop");
			this._refreshStartStopBtnState();
		};

		var failCallback = function() {
			this._showErrorMessageBox(this.getModelProperty("CAMPAIGN_STOP_FAILED"));
		};

		var alwaysCallback = function() {
			appController.setAppBusy(false);
		};

		appController.setAppBusy(true);

		var ctx = this.byId("inputForm").getBindingContext().getObject();
		com.sap.hana.cloud.samples.benefits.util.AjaxUtil.asynchPostJSON(this, 'OData.svc/stopCampaign?campaignId='
				+ ctx.Id, null, doneCallback, failCallback, alwaysCallback);
	},

	_showErrorMessageBox : function(message) {
		sap.m.MessageBox.show(message, sap.m.MessageBox.Icon.ERROR, "{b_i18n>ERROR_TITLE}", [sap.m.MessageBox.Action.OK]);
	}

});
