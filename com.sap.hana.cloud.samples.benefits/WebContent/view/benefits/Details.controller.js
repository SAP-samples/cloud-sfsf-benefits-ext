sap.ui.controller("com.sap.hana.cloud.samples.benefits.view.benefits.Details", {
	onInit : function() {
		sap.ui.getCore().getEventBus().subscribe("app", "benefitsDetailsRefresh", this._refreshHandler, this);

		var moreInfoLink = this.getView().byId("moreInfoLink");

		moreInfoLink.addEventDelegate({

			onAfterRendering : function(e) {
				$("[data-sap-ui=" + moreInfoLink.getId() + "]").attr('tabindex', 0);
			},

			onkeydown : function(e) {
				var code = e.which;
				// 13 = Return, 32 = Space
				if ((code === 13) || (code === 32)) {
					moreInfoLink.firePress();
				}
			}
		});
		moreInfoLink.addStyleClass("itemFocus");
		this.showJamWidget();

	},
	onBeforeRendering : function() {
		this.hideLogout();
	},
	hideLogout : function() {
		this.byId("logoutButton").setVisible(appController._hasLogoutButton());
	},

	logoutButtonPressed : function(evt) {
		sap.ui.getApplication().onLogout();
	},
	linkPressed : function(evt) {
		var control = sap.ui.getCore().byId(evt.getParameters().id);
		var link = control.getBindingContext().getObject().Link;
		if (link) {
			sap.m.URLHelper.redirect(link, true);
		}
	},
	formatValue : function(value) {
		var message = sap.ui.getCore().getModel("b_i18n").getProperty("BENEFITS_VALUE").formatPropertyMessage(value);
		return message;
	},
	_refreshHandler : function(channelId, eventId, data) {
		this.getView().setBindingContext(data.context);
		this.getView().setModel(data.context.getModel());
		var sName = data.context.getObject().Name.trim();
		var sUrl;
		//The following values are hard coded for simplicity and for the purpose of the course. 
		//When building your own application you would want to replace these values with external objects, you have created for your own system.
		switch (sName) {
			case "Amazon Gift Voucher" :
				sUrl = "/ExternalObjects('KpauCPD8WUyRcAewpCvxk0')"; // Amazon
				break;
			case "Charity Donations" :
				sUrl = "/ExternalObjects('nfxFzwB9hZkQ9Y0VCfsRI9')"; // Charity Donations
				break;
			case "Liverpool FC" :
				sUrl = "/ExternalObjects('YPzfc7cFx6MgNgfn77wNYh')"; // Liverpool
				break;
			case "Health Center" :
				sUrl = "/ExternalObjects('THdDvLeg2IFdrnmLmIfDn7')"; // Health Center
				break;
			default :
				throw "Unexpected benefit name[" + sName + "]!";
		}

		this.oJamView.bindElement(sUrl);
	},
	showJamWidget : function() {
		jQuery.sap.registerModulePath("sap.jam", "jam_proxy/sap_jam_static/javascripts/ui5/sap/jam");

		jQuery.sap.require("sap.jam.ProxyHelper");
		sap.jam.ProxyHelper.setProxy("jam_proxy/sap_jam_static");
		var oJamModel = new sap.ui.model.odata.ODataModel("jam_proxy/sap_jam_odata", true);
		oJamModel.setDefaultCountMode(sap.ui.model.odata.CountMode.None);
		var oView = new sap.ui.view('jamwidget', {
			viewName : "sap.jam.Feed",// "sap.jam.GroupList",
			type : sap.ui.core.mvc.ViewType.JS,
			width : "100%"
		});
		oView.setModel(oJamModel);
		//		
		this.byId('BenefitsDetailsPage').addContent(oView);
		this.oJamView = oView;
	}
});
