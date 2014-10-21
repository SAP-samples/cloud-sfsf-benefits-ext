jQuery.sap.require("sap.jam.JamFeedListItem");
jQuery.sap.require("sap.m.MessageBox");

sap.ui.controller("sap.jam.Feed", {

  onInit: function() {
    var oFeed = this.oView.oFeedList,
      oFeederInput = this.oView.oFeederInput,
      oFeederButton = this.oView.oFeederButton;

    var oFeedItemTemplate = new sap.jam.JamFeedListItem({
      feedId: "{Id}",
      sender: "{Creator/FullName}",
      blurb: "{Text}",
      title: "{Action}",
      info: "{Group/Name}",
      groupId: "{Group/Id}",
      liked: "{Liked}",
      bookmarked: "{Bookmarked}",
      timestamp: {
        path: "CreatedAt",
        type: new sap.ui.model.type.DateTime({pattern: "yyyy-MM-ddTHH:mm:ssZ"})
      },
      canDelete: "{CanDelete}",
      canLike: "{CanLike}",
      canComment: "{CanComment}",
      hasHtml: true,
      creator: "{Creator}",
      iconPress : this.showSender, 
      senderPress : this.showSender,      
      commentPath: 'Replies',
      iconDensityAware: false,
      icon: {
        path: "ThumbnailImage/__metadata/media_src",
        formatter: function(sUrl) {
          return sUrl ? sap.jam.ProxyHelper.getODataUrl('/' + sUrl, null, this.getModel()) : undefined;
        }
      },
      repliesCount: '{RepliesCount}',
      textWithPlaceholders: '{TextWithPlaceholders}'
    });
    oFeed.bindAggregation('items', {
      path: 'FeedEntries', 
      template: oFeedItemTemplate,
      parameters: {
        expand: 'Group,Creator,ThumbnailImage',
        custom: {internal_api_subject_to_change: "true"},
      }
    });
    
    oFeederButton.attachPress(this.doPost, this);

    this.doPolling();  
  },

  doPost: function(oEvent) {
    var oFeederInput = this.oView.oFeederInput,
      oFeed = this.oView.oFeedList,
      oBusyDialog = new sap.m.BusyDialog();
    var text = oFeederInput.getValue();
    if(text) {
      oBusyDialog.open();
      var oContext  = oFeed.getBindingContext();
      var oModel   = this.getView().getModel();
      var fError = function(oError) {
        oBusyDialog.close();
        sap.jam.StringHelper.showErrorMessage(oError);
      };

      if(this.isExobjFeed()) {
        var sUrl = oModel.getProperty('__metadata/uri', oContext);
        var postBody = {
          Content: text,
          Object: {
            __metadata: {
              uri: sUrl
            }
          }
        };

        oModel.create("/Activities", postBody, oContext, function() {
          oBusyDialog.close();
          oFeederInput.setValue('');
          oModel.refresh();
        }, fError);        
      } else {
        var postBody = {Text: text};
        oModel.create("FeedEntries", postBody, oContext, function() {
          oBusyDialog.close();
          oFeederInput.setValue('');
        }, fError);
      }
    } else {
      sap.m.MessageBox.alert(sap.jam.StringHelper.getText('post.enter_message_to_share'));
    }    
  },

  showSender: function(oEvent) {
    var id = oEvent.oSource.getCreator().Id;
    sap.jam.JamFeedListItemRenderer.goToJamApp("/profile/wall/" + id, oEvent.oSource.getModel());
  },

  doPolling: function() {
    var self = this,
      oModel = this.getView().getModel();
    if(!oModel) {
      setTimeout($.proxy(self.doPolling, self), 30000);
      return;
    }
    oModel.read('FeedEntries', {
      urlParameters: {
        "$top": "1",
        "internal_api_subject_to_change": "true"
      }, 
      success: function(oData, response) {
        if(oData && oData.results) {
          var newId = oData.results[0].Id;
          if(self.freshestId && self.freshestId !== newId) {
            self.byId('refreshBar').setVisible(true);
          } else {
            self.freshestId = newId;
            setTimeout($.proxy(self.doPolling, self), 30000);
          }
        }
      }
    });
  },  
  
  handleRefresh: function(oEvent) {
    oEvent.getSource().setVisible(false);
    this.getView().getModel().refresh(false);
    this.freshestId = undefined;
    this.doPolling();
  },

  isExobjFeed: function()  {
    var oContext = this.getView().getBindingContext();
    return oContext && (oContext.getPath().indexOf('ExternalObjects') > -1);
  },

  onBeforeRendering: function() {
  },

  onAfterRendering: function() {
  }

});
