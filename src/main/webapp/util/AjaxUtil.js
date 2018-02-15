jQuery.sap.declare("com.sap.hana.cloud.samples.benefits.util.AjaxUtil");

com.sap.hana.cloud.samples.benefits.util.AjaxUtil = {

	asynchGetJSON : function(thiz, sPath, fDoneCallback, fFailCallback, fAlwaysCallback) {
		return jQuery.ajax({
			url : sPath,
			type : "GET",
			dataType : "json",
			context : thiz
		}).done(fDoneCallback).fail(fFailCallback).always(fAlwaysCallback);
	},

	asynchPostJSON : function(thiz, sPath, oData, fDoneCallback, fFailCallback, fAlwaysCallback) {
		return jQuery.ajax({
			url : sPath,
			type : "POST",
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(oData),
			context : thiz
		}).done(fDoneCallback).fail(fFailCallback).always(fAlwaysCallback);
	},

	asynchDelete : function(thiz, sPath, fDoneCallback, fFailCallback, fAlwaysCallback) {
		return jQuery.ajax({
			url : sPath,
			type : "DELETE",
			context : thiz
		}).done(fDoneCallback).fail(fFailCallback).always(fAlwaysCallback);
	},
};
