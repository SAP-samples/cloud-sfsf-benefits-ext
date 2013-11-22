jQuery.sap.declare("com.sap.hana.cloud.samples.benefits.common.ListHelper");
com.sap.hana.cloud.samples.benefits.common.ListHelper = function() {
};
com.sap.hana.cloud.samples.benefits.common.ListHelper.prototype = jQuery.sap.newObject(sap.ui.base.Object.prototype);

sap.ui.base.Object.defineClass("com.sap.hana.cloud.samples.benefits.common.ListHelper", {
	baseType : "sap.ui.base.Object",
	publicMethods : [
	// methods
	"selectListItem"]
});
com.sap.hana.cloud.samples.benefits.common.ListHelper.prototype.selectListItem = function(list, itemIndex, defaultPageId) {
	var items = list.getItems();
	if (items[itemIndex]) {
		items[itemIndex].setSelected(true);
		list.fireSelect({
			listItem : items[itemIndex],
			id : list.getId()
		});
	} else {
		sap.ui.getCore().getEventBus().publish("nav", "to", {
			id : defaultPageId
		});
	}
};