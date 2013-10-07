sap.ui.controller("com.sap.benefits.management.view.employees.Details", {
    monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
    onInit: function() {

    },
    onAfterRendering: function() {
    },
    formatTypes: function(types) {
        var text = "";
        for (type in types) {
            text += types[type].quantity + 'x' + types[type].name + ", ";
        }
        return text.slice(0, -2);
    },
    groupBenefits: function(oContext) {
        var date = new Date(oContext.getProperty("date"));
        return {
            key: date.getMonth() + 1,
            text: this.monthNames[date.getMonth()]
        }
    }
});
