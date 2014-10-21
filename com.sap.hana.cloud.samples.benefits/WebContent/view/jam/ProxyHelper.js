jQuery.sap.declare('sap.jam.ProxyHelper');

sap.jam.ProxyHelper = {
  setProxy: function(p) {
    this.proxy = p;
  },

  getProxy: function() {
    return this.proxy ? this.proxy : "";
  },

  addProxy: function(path) {
    join = path.match(/^\//) ? "" : "/";
    return this.getProxy() + join + path;
  },

  getODataUrl: function(path,context,model) {
    model = context ? context.getModel() : model;
    model = model ? model : sap.ui.getCore().getModel();
    var auth = model.getHeaders()['Authorization'];
    var uri = new URI(model.sServiceUrl + (context ? context.getBindingContextPath() : "") + path);
    if (auth) {
      uri.addQuery('oauth_token', auth.split(' ')[1]);
    }
    return uri.toString();
  }
}

