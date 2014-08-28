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
	onAfterRendering : function() {
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
		var isvalidPeriod = this._isValidDatePeriod(startDate, endDate);
		if (isvalidPeriod) {
			this.editCampaignDialog.close();
			var ctx = this.byId("inputForm").getBindingContext().getObject();
			var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
				style : "full",
				pattern : "yyyy-MM-dd'T'HH:mm:ss"
			});
			var StartDate = dateFormat.format(sap.ui.getCore().byId("editCampaignDialog--startDateCtr").getDateValue());
			var EndDate = dateFormat.format(sap.ui.getCore().byId("editCampaignDialog--endDateCtr").getDateValue());
			jQuery.ajax({
				url : 'OData.svc/editCampaign?startDate=datetime\'' + StartDate + '\'&endDate=datetime\'' + EndDate
						+ '\'&campaignid=' + ctx.Id,
				type : 'post',
				dataType : 'json',
				success : jQuery.proxy(function(data) {
					sap.m.MessageToast.show(this.getModelProperty("DATA_SAVED_MSG"));
					this.fireModelChanged("edit");
				}, this),
				statusCode : {
					400 : function(xhr, error) {
						sap.m.MessageToast.show(xhr.responseText);
					}
				}
			});
		} else {
			$(".errorContainer").removeClass("displayNone");
			var message = this.getModelProperty(!startDate || !endDate ? "INVALID_DATES_MSG" : "INVALID_PERIOD_MSG");
			sap.ui.getCore().byId("editCampaignDialog--errorStatusText").setText(message);
		}
	},
	_isValidDatePeriod : function(startDate, endDate) {
		return startDate && endDate && startDate.getTime() < endDate.getTime();
	},
	_deleteCampaignBtnPressed : function(evt) {
		var campaignId = this.byId("inputForm").getBindingContext().getObject().Id;
		var campaignName = this.byId("inputForm").getBindingContext().getObject().Name;
		var message = sap.ui.getCore().getModel("b_i18n").getProperty("DELETE_CAMPAIGN_MSG").formatPropertyMessage(
				campaignName);
		sap.m.MessageBox.confirm(message, jQuery.proxy(function(action) {
			if (action === sap.m.MessageBox.Action.OK) {
				this.getView().setBusy(true);
				jQuery.ajax({
					url : 'OData.svc/deleteCampaign?campaignId=' + campaignId,
					type : 'delete',
					success : jQuery.proxy(function(data) {
						this.fireModelChanged("delete");
					}, this),
					complete : jQuery.proxy(function() {
						this.getView().setBusy(false);
					}, this)
				});
			}
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

		appController.setAppBusy(true);
		var ctx = this.byId("inputForm").getBindingContext().getObject();
		jQuery.ajax({
			url : 'OData.svc/canStartCampaign?campaignId=' + ctx.Id,
			type : 'get',
			dataType : 'json',
			success : jQuery.proxy(function(data) {
				if (!data.d.canStartCampaign.canBeStarted) {
					var message = sap.ui.getCore().getModel("b_i18n").getProperty("UNABLE_START_MORE_CAMPAIGNS_MSG")
							.formatPropertyMessage(data.d.canStartCampaign.startedCampaignName);
					this._showErrorMessageBox(message);
					return;
				}

				this._requestStartCampaign();
			}, this),
			complete : function() {
				appController.setAppBusy(false);
			},
			contentType : "application/json; charset=utf-8"
		});
	},

	_validateCampaignDataExist : function() {
		var campData = this.byId("inputForm").getBindingContext().getObject();
		return campData.Name && campData.StartDate && campData.EndDate;
	},
	_requestStopCampaign : function() {
		appController.setAppBusy(true);
		var ctx = this.byId("inputForm").getBindingContext().getObject();
		jQuery.ajax({
			url : 'OData.svc/stopCampaign?campaignId=' + ctx.Id,
			type : 'post',
			dataType : 'json',
			success : jQuery.proxy(function(data) {
				sap.m.MessageToast.show(this.getModelProperty("STOPPED_CAMPAIGN_MSG"));
				this.fireModelChanged("stop");
				this._refreshStartStopBtnState();
			}, this),
			complete : function() {
				appController.setAppBusy(false);
			},
			contentType : "application/json; charset=utf-8"
		});
	},
	_requestStartCampaign : function() {
		var ctx = this.byId("inputForm").getBindingContext().getObject();
		jQuery.ajax({
			url : 'OData.svc/startCampaign?campaignId=' + ctx.Id,
			type : 'post',
			dataType : 'json',
			success : jQuery.proxy(function(data) {
				sap.m.MessageToast.show(this.getModelProperty("STARTED_CAMPAIGN_MSG"));
				this.fireModelChanged("start");
				this._refreshStartStopBtnState();
			}, this),
			contentType : "application/json; charset=utf-8"
		});
	},
	_showErrorMessageBox : function(message) {
		sap.m.MessageBox.show(message, sap.m.MessageBox.Icon.ERROR, null, [sap.m.MessageBox.Action.OK]);
	}

});
