jQuery.sap.declare("com.sap.hana.cloud.samples.benefits.common.SearchFilter");
com.sap.hana.cloud.samples.benefits.common.SearchFilter = function() {
};
com.sap.hana.cloud.samples.benefits.common.SearchFilter.prototype = jQuery.sap.newObject(sap.ui.base.Object.prototype);

sap.ui.base.Object.defineClass("com.sap.hana.cloud.samples.benefits.common.SearchFilter", {
	baseType : "sap.ui.base.Object",
	publicMethods : [
	// methods
	"applySearch"]
});
com.sap.hana.cloud.samples.benefits.common.SearchFilter.prototype.applySearch = function(list, searchField, modelProperty, defaultPageId) {
	var showSearch = (searchField.getValue().length !== 0);
	var binding = list.getBinding("items");

	if (binding) {
		if (showSearch) {
			var filterName = new sap.ui.model.Filter(modelProperty, sap.ui.model.FilterOperator.Contains, searchField.getValue());
			binding.filter([filterName]);
		} else {
			binding.filter([]);
		}
	}
};