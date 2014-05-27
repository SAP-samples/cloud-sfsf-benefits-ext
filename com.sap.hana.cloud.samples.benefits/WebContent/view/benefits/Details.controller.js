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
	}
});
