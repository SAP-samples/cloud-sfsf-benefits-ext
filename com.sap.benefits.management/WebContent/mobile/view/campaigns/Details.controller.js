sap.ui.controller("com.sap.benefits.management.view.campaigns.Details", {
    onInit: function() {
        this.getView().addEventDelegate({
            onBeforeShow: function(evt) {
                this.setBindingContext(evt.data.context);
                if (!evt.data.context.getObject().startDate) {
                    this.byId("startDateCtr").setValue(null);
                    this.byId("startDateCtr").rerender();
                }
                if (!evt.data.context.getObject().endDate) {
                    this.byId("endDateCtr").setValue(null);
                    this.byId("endDateCtr").rerender();
                }
            }
        }, this.getView());
    },
    onAfterRendering: function() {
    },
    onBeforeRendering: function() {
    },
    saveButtonPressed: function(evt) {
        jQuery.sap.require("sap.m.MessageToast");
        jQuery.sap.require("sap.ui.core.format.DateFormat");

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
            data: JSON.stringify({name: this.byId("nameCtr").getValue(),
                                  id: ctx.id,
                                  startDate: dateFormat.format(this.byId("startDateCtr").getDateValue()),
                                  endDate: dateFormat.format(this.byId("endDateCtr").getDateValue()),
                                  points: this.byId("pointsCtr").getValue(),
                                  active: this.byId("stateCtr").getSelected()
            }),
            error: function(xhr, error) {
                sap.m.MessageToast.show(xhr.responseText);
            }
        });
    },
    formatDate: function(data) {
        if (data) {
            jQuery.sap.require("sap.ui.core.format.DateFormat");
            var date = new Date(data);
            return sap.ui.core.format.DateFormat.getDateInstance({style: "full", pattern: "M/d/yy"}).format(date);
        } else {
            return '';
        }
    }
});
