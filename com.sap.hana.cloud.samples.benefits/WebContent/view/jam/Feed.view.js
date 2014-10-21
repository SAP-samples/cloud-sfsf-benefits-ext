jQuery.sap.require("sap.jam.StringHelper");
jQuery.sap.require('sap.jam.ProxyHelper');
jQuery.sap.require("sap.jam.AutocompleteTextArea");

sap.ui.jsview("sap.jam.Feed", {

  getControllerName : function() {
    return "sap.jam.Feed";        
  },

  createContent : function(oController) {
    var oVBox = new sap.m.VBox();

    this.oFeederInput = new sap.jam.AutocompleteTextArea({
      width: '100%',
      height: '48px',
      layoutData: new sap.m.FlexItemData({growFactor: 1})
    });
    this.oFeederButton = new sap.m.Button({
      icon: "sap-icon://feeder-arrow"
    });
    var user = null;
    // this.getModel().read('/Self',{
    //   success: function(x) { user=x; },
    //   async: false,
    // });
    // imageUrl = sap.jam.ProxyHelper.getODataUrl( "/Members('" + user.results.Id + "')/ThumbnailImage/$value", null, this.getModel());
    this.oProfilePhoto = new sap.m.Image({
      src: {
        path: '/Self/Id',
        formatter: function(sId) {
          return sId ? sap.jam.ProxyHelper.getODataUrl( "/Members('" + sId + "')/ThumbnailImage/$value", null, this.getModel()) : undefined;
        }
      },
      densityAware: false,
      width: "48px",
      height: "48px",
    });    
    var oFeederBar = new sap.m.HBox({
      justifyContent: sap.m.FlexJustifyContent.Center,
        alignItems: sap.m.FlexAlignItems.Center,
        fitContainer: true,
        items: [this.oProfilePhoto,this.oFeederInput,this.oFeederButton]
    });
    oVBox.addItem(oFeederBar);
    oVBox.addItem(new sap.m.PullToRefresh(this.createId('refreshBar'), {
      visible: false,
      refresh: function(oEvent) {
        oController.handleRefresh(oEvent);
      }
    }));

    this.oFeedList = new sap.m.List(this.createId("feedControl"), {
      growing: true,
      showNoData: false
    });
    oVBox.addItem(this.oFeedList);
    return oVBox;
  }
});
