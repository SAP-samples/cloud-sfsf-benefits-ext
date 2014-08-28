jQuery.sap.declare("com.sap.hana.cloud.samples.benefits.util.Helper");

jQuery.sap.require("sap.ui.core.format.DateFormat");

com.sap.hana.cloud.samples.benefits.util.Helper = {

	synchGetJSON : function(sPath, fSuccess, fError, fComplete) {
		var result = {
			data : null
		};
		var closureSuccFunc = function(result) {
			return jQuery.proxy(function(oRespData, textStatus, jqXHR) {
				fSuccess(oRespData, textStatus, jqXHR);
				result.data = oRespData;
			}, this);
		};
		jQuery.ajax({
			async : false,
			url : sPath,
			type : 'GET',
			dataType : 'json',
			success : closureSuccFunc(result),
			error : fError,
			complete : fComplete
		});
		return result.data;
	},
};
