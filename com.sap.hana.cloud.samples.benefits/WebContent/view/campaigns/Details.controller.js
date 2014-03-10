jQuery.sap.require("sap.ui.core.ValueState");
jQuery.sap.require("sap.ui.core.format.DateFormat");
jQuery.sap.require("sap.m.MessageBox");
jQuery.sap.require("sap.m.MessageToast");
sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.campaigns.Details", {
	onInit : function() {
		sap.ui.getCore().getEventBus().subscribe("app", "campaignDetailsRefresh", this._refreshHandler, this);
		formatter = sap.ui.core.format.DateFormat;
		this.getView().addEventDelegate({
	         onBeforeShow: function(evt) {
	        	
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
		if(active) {
			this.byId("stateCtr").removeStyleClass("red");
			this.byId("stateCtr").addStyleClass("green");
			return this.getModelProperty("CAMPAIGN_STATUS_ACTIVE");
		}
		else{
			this.byId("stateCtr").removeStyleClass("green");
			this.byId("stateCtr").addStyleClass("red");
			return this.getModelProperty("CAMPAIGN_STATUS_INACTIVE");
		}
	},
	getDateObject : function(date,format){
		var dateObject = formatter.getDateInstance({
			style : "full",
			pattern : "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'"
		}).parse(date);
		return formatter.getDateInstance({
			style : "full",
			pattern : format
		}).format(dateObject);
	},
	formatDate : function(date) {
		if (date) {
			return this.getDateObject(date,"MMM d, y");
		} else {
			var notSetMsg = this.getModelProperty("NOT_SET_MSG");
			return notSetMsg;
		}
	},
	formatDateTxt : function(date) {
		if (date) {
			return this.getDateObject(date,"dd.MM.yy");
		} else {
			return undefined;
		}
	},
	formatBenefitPoints : function(points){
		var message = sap.ui.getCore().getModel("b_i18n").getProperty("CAMPAIGN_ENTITLEMENT_VALUE").formatPropertyMessage(points);
		return message;
		
	},
	startStopButtonPressed : function(evt) {
		if (evt.getSource().state === 'stop') {
			this._requestStopCampaign();
		} else {
			this._startCampaign();
		}
	},
	changeDatesButtonPressed : function(evt) {
		if (!this.editCampaignDialog) {
			this.editCampaignDialog = sap.ui.xmlfragment("editCampaignDialog", "view.campaigns.editCampaignDialog", this);
		}
		var startDate = this.byId("inputForm").getBindingContext().getObject().startDate;
		var endDate = this.byId("inputForm").getBindingContext().getObject().endDate;
		sap.ui.getCore().byId("editCampaignDialog--startDateCtr").setValue(this.formatDateTxt(startDate));
		sap.ui.getCore().byId("editCampaignDialog--endDateCtr").setValue(this.formatDateTxt(endDate));
		sap.ui.getCore().getEventBus().publish("nav", "virtual");
		this.editCampaignDialog.open();
	},
	cancelButtonPressed : function(){
		this.editCampaignDialog.close();
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
	},
	hideLogout : function(){
		this.byId("logoutButton").setVisible(appController._hasLogoutButton());
	},	
	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	},
	getModelProperty : function(msgId){
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
				pattern : "yyyy-MM-dd'T'HH:mm:ss'Z'"
			});
			jQuery.ajax({
				url : 'api/campaigns/edit/' + ctx.id,
				type : 'post',
				dataType : 'json',
				success : jQuery.proxy(function(data) {
					sap.m.MessageToast.show(this.getModelProperty("DATA_SAVED_MSG"));
					this.fireModelChanged("edit");
				}, this),
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify({
					id : ctx.id,
					startDate : dateFormat.format(sap.ui.getCore().byId("editCampaignDialog--startDateCtr").getDateValue()),
					endDate : dateFormat.format(sap.ui.getCore().byId("editCampaignDialog--endDateCtr").getDateValue())
				}),
				statusCode : {
					400 : function(xhr, error) {
						sap.m.MessageToast.show(xhr.responseText);
					}
				}
			});
		} else {
			$(".errorContainer").removeClass("displayNone");
			if (!startDate || !endDate) {
				sap.ui.getCore().byId("editCampaignDialog--errorStatusText").setText(this.getModelProperty("INVALID_DATES_MSG"));
			} else {
				sap.ui.getCore().byId("editCampaignDialog--errorStatusText").setText(this.getModelProperty("INVALID_PERIOD_MSG"));
			}
		}
	},
	_isValidDatePeriod : function(startDate, endDate) {
		if (startDate && endDate && startDate.getTime() < endDate.getTime()) {
			return true;
		} else {
			return false;
		}
	},
	_deleteCampaignBtnPressed : function(evt) {
		var campaignId = this.byId("inputForm").getBindingContext().getObject().id;
		var campaignName = this.byId("inputForm").getBindingContext().getObject().name;
		var message = sap.ui.getCore().getModel("b_i18n").getProperty("DELETE_CAMPAIGN_MSG").formatPropertyMessage(campaignName);
		sap.m.MessageBox.confirm(message, jQuery.proxy(function(
				action) {
			if (action === sap.m.MessageBox.Action.OK) {
				this.getView().setBusy(true);
				jQuery.ajax({
					url : 'api/campaigns/' + campaignId,
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
		var isCampaignStarted = this.getView().getBindingContext().getObject().active;
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
		if (this._validateCampaignDataExist()) {
			appController.setAppBusy(true);
			var ctx = this.byId("inputForm").getBindingContext().getObject();
			jQuery.ajax({
				url : 'api/campaigns/start-possible/' + ctx.id,
				type : 'get',
				dataType : 'json',
				success : jQuery.proxy(function(data) {
					if (data.canBeStarted) {
						this._requestStartCampaign();
					} else {
						var message = sap.ui.getCore().getModel("b_i18n").getProperty("UNABLE_START_MORE_CAMPAIGNS_MSG").formatPropertyMessage(data.startedCampaignName);
						this._showErrorMessageBox(message);
					}
				}, this),
				complete : function() {
					appController.setAppBusy(false);
				},
				contentType : "application/json; charset=utf-8",
			});
		} else {
			this._showErrorMessageBox(this.getModelProperty("UNABLE_START_CAMPAIGN_MSG"));
		}
	},
	_validateCampaignDataExist : function() {
		var campData = this.byId("inputForm").getBindingContext().getObject();
		if (campData.name && campData.startDate && campData.endDate && campData.points) {
			return true;
		}
		return false;
	},
	_requestStopCampaign : function() {
		var ctx = this.byId("inputForm").getBindingContext().getObject();
		jQuery.ajax({
			url : 'api/campaigns/stop/' + ctx.id,
			type : 'post',
			dataType : 'json',
			success : jQuery.proxy(function(data) {
				sap.m.MessageToast.show(this.getModelProperty("STOPPED_CAMPAIGN_MSG"));
				this.fireModelChanged("stop");
				this._refreshStartStopBtnState();
			}, this),
			contentType : "application/json; charset=utf-8"
		});
	},
	_requestStartCampaign : function() {
		var ctx = this.byId("inputForm").getBindingContext().getObject();
		jQuery.ajax({
			url : 'api/campaigns/start/' + ctx.id,
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
	},
	
});
