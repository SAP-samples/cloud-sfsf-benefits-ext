jQuery.sap.require("jquery.sap.resources");
jQuery.sap.require("sap.jam.ProxyHelper");
jQuery.sap.declare("sap.jam.StringHelper");

jamApp = {
  t: function(key, args) {
    if(args && typeof args === 'object') {
      var oldArgs = args;
      args = [];
      for(var prop in oldArgs) {
        args.push(oldArgs[prop]);
      }
    }
    return sap.jam.StringHelper.getBundle().getText(key, args);
  }
};

sap.jam.StringHelper = {
  oBundle: null,

  getBundle: function() {
    if(sap.jam.StringHelper.oBundle === null) {
      var sLocale = sap.ui.getCore().getConfiguration().getLanguage();
      sap.jam.StringHelper.oBundle = jQuery.sap.resources({url: sap.jam.ProxyHelper.addProxy('/widget/ui5/i18n.properties'), locale:sLocale});
    }
    return sap.jam.StringHelper.oBundle;
  },

  getText: function(key, params) {
    return sap.jam.StringHelper.getBundle().getText(key, params);
  },

  showErrorMessage: function(oError) {  	  
    if(sap.jam.StringHelper.messageOpen) {
      return; //don't show message box if one is already open, otherwise some errors (e.g. 429) can spam a bunch of message boxes
    }
    sap.jam.StringHelper.messageOpen = true;

    var title = oError.message,
      resp,
      statusCode,
      message;
    try {
      if(oError.getParameter) {
        title = oError.getParameter('message');
        statusCode = oError.getParameter('statusCode');
        resp = JSON.parse(oError.getParameter('responseText'));
      } else if(oError.response) {
        resp = JSON.parse(oError.response.body);
      } 
    } catch (e) {
      // JSON didn't parse correctly, so ignore it.
    }      
    if(!statusCode) {
    	statusCode = (oError && oError.response && oError.response.statusCode) ? oError.response.statusCode : undefined;    	
    	statusCode = statusCode || ((oError && oError.status ) ? oError.status : undefined);    	
    }        
    if(statusCode == 429 || statusCode == 509) {
    	message = sap.jam.StringHelper.getText('widget.error.api_limit_exceeded');
    }
    else if(statusCode == 500) {
      message = sap.jam.StringHelper.getText('common.unknown_error');
    } 
    else if(statusCode == 503) {
      message = sap.jam.StringHelper.getText('widget.error.maintenance');      
    } 
    else if(resp && resp['error'] && resp['error']['message'] && resp['error']['message']['value']) {
      message = resp['error']['message']['value'];
    } 
    
    sap.m.MessageBox.alert(message, function() {
      sap.jam.StringHelper.messageOpen = false;
    }, title);
  }
};
