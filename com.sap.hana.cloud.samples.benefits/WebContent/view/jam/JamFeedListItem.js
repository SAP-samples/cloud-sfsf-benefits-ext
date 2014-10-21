jQuery.sap.require('sap.jam.TimeAgoHelper');
jQuery.sap.require('sap.jam.ProxyHelper');
jQuery.sap.require('sap.jam.AutocompleteTextArea');
jQuery.sap.require('sap.jam.MemberLink');

sap.m.FeedListItem.extend("sap.jam.JamFeedListItem", {
  metadata : {
    properties : {
      "feedId": "string",
      "title": "string",
      "blurb": "string",
      'liked': 'boolean',
      'bookmarked': 'boolean',
      'canDelete': 'boolean',
      'canLike': 'boolean',
      'canComment': 'boolean',
      'source': 'string',
      'creator' : 'object',
      'commentPath': 'string',
      'hasHtml': 'boolean',
      'groupId' : 'string',
      'repliesCount': 'int',
      'textWithPlaceholders': 'string',
      'textExpanded': 'boolean'
    },

    aggregations : {
      "_footer": {type: "sap.m.HBox", multiple: false, visibility: "hidden"},
      "_comments": {type: "sap.m.List", multiple: false, visibility: "hidden"},
      "_replyBar": {type: "sap.m.HBox", multiple: false, visibility: "hidden"},
      "_moreMenu": {type: "sap.m.ActionSheet", multiple: false, visibility: "hidden"},
      "mentionLinks": {type: "sap.jam.MemberLink", multiple: true}
    }
  },

  init: function() {
    this.setAggregation("_comments", new sap.m.List({
      showNoData: false,
      showSeparators: sap.m.ListSeparators.None,
      growing: true,
      growingThreshold: 4,
      growingScrollToLoad: false 
    }));

    this.initFooter();
    this.initReplyBox();    
    this.initMoreMenu();    
    this.initBookmarkButton();   
    this.initDeleteButton();
  },

  initFooter: function() {
    var self = this;
    var oFooter = new sap.m.HBox({
      justifyContent: sap.m.FlexJustifyContent.SpaceBetween
    });
    this.setAggregation("_footer", oFooter);

    this.oLikesText = new sap.m.Text({
      layoutData: new sap.m.FlexItemData({growFactor: 0.7})
    });
    this.oLikesText.bindText('LikesCount', function(count) {
      if(count === 1) {
        return sap.jam.StringHelper.getText('explore.like_count.one');
      }
      return sap.jam.StringHelper.getText('explore.like_count.other', [count]);
    }); 
    oFooter.addItem(this.oLikesText); 
    
    this.oLikeLink = new sap.m.Link({
      press: function() {
        var putBody = {Liked: !self.getLiked()};
        var path = self.getBindingContextPath();
        var oModel   = this.getModel();
        oModel.update(path, putBody, {
          merge: true,
          fnSuccess: function() { },
          fnError: sap.jam.StringHelper.showErrorMessage
        }); 
      }
    });
    this.oLikeLink.addStyleClass('feed_action_link');
    this.oLikeLink.bindProperty('text', {
      path: 'Liked', 
      formatter: function(liked) {
        var key = liked === true ? "common.unlike" : "common.like";
        return sap.jam.StringHelper.getText(key);
      }
    });
    oFooter.addItem(this.oLikeLink);  
  },

  initMoreMenu: function() {
    var self = this,
      oFooter = this.__getFooterControl(),
      oMoreMenu = new sap.m.ActionSheet();

    this.setAggregation("_moreMenu", oMoreMenu);
    
    var oMoreLink = new sap.m.Link({
      text: sap.jam.StringHelper.getText('common.more'),
      press: function(evt) {
        oMoreMenu.openBy(this);
      }
    });    
    oMoreLink.addStyleClass('feed_action_link');
    oFooter.addItem(new sap.m.Text({text: '•'}));
    oFooter.addItem(oMoreLink);
  },

  initBookmarkButton: function() {
    var self = this,
      oMoreMenu = this.__getMoreMenuControl();

    var oBookmarkButton = new sap.m.Button({
      icon: "sap-icon://bookmark",
      press: function() {
        var putBody = {Bookmarked: !self.getBookmarked()};
        var path = self.getBindingContextPath();
        var oModel   = this.getModel();
        oModel.update(path, putBody, {
          merge: true,
          fnSuccess: function() {
          },
          fnError: sap.jam.StringHelper.showErrorMessage
        });
      }
    });
    oBookmarkButton.bindProperty('text', {
      path: 'Bookmarked', 
      formatter: function(bookmarked) {
        var key = (bookmarked === true || self.getBookmarked()) ? "feed_item_footer.unbookmark" : "feed_item_footer.bookmark";
        return sap.jam.StringHelper.getText(key)
      }
    });  
    oMoreMenu.addButton(oBookmarkButton); 
  },

  initDeleteButton: function() {
    var self = this,
      oMoreMenu = this.__getMoreMenuControl(),
      oCommentList = this.__getCommentListControl();

    var oDeleteButton = new sap.m.Button({
      icon: "sap-icon://delete",
      text: sap.jam.StringHelper.getText('feed_item_footer.delete'),
      visible: '{CanDelete}',
      press: function() {
        if(!self.getCanDelete()) {
          return;
        }
        sap.m.MessageBox.confirm(sap.jam.StringHelper.getText('trash.confirmation.body'), 
          function(oAction) {
            if(oAction !== sap.m.MessageBox.Action.OK) {
              return;
            }
            if(oCommentList) {
              oCommentList.unbindAggregation('items');
            }
            var path = self.getBindingContextPath();
            var oModel   = this.getModel();
            oModel.remove(path, {
              fnError: sap.jam.StringHelper.showErrorMessage
            });
          }, 
          sap.jam.StringHelper.getText('trash.confirmation.title'));
        }
    });
    oMoreMenu.addButton(oDeleteButton);
  },

  initReplyBox: function() {
    var self = this,
    oFooter = this.__getFooterControl();

    this.oShowReplyLink = new sap.m.Link({
      //icon: "sap-icon://comment",
      text: sap.jam.StringHelper.getText('feed_item_footer.reply'),
      press: function() {
        self.__getReplyBarControl().setVisible(true);
      }
    });
    this.oShowReplyLink.addStyleClass('feed_action_link');
    oFooter.addItem(new sap.m.Text({text: '•'}));
    oFooter.addItem(this.oShowReplyLink);


    this.oReplyInput = new sap.jam.AutocompleteTextArea({
      width: '100%',
      height: '48px',
      layoutData: new sap.m.FlexItemData({growFactor: 1}),
    });
    this.oReplyButton = new sap.m.Button({
      icon: "sap-icon://feeder-arrow"
    });
    this.oReplyCancelButton = new sap.m.Button({
      icon: "sap-icon://sys-cancel",
      press: function() {
        self.__getReplyBarControl().setVisible(false);
      }
    });    
    this.setAggregation('_replyBar', new sap.m.HBox({
      justifyContent: sap.m.FlexJustifyContent.Center,
      alignItems: sap.m.FlexAlignItems.Center,
      fitContainer: true,
      items: [this.oReplyInput,this.oReplyButton,this.oReplyCancelButton],
      visible: false
    }));

    this.oReplyButton.attachPress(function(oEvent) {
      var text = self.oReplyInput.getValue();
      if(text) {
        var postBody = {Text: text};
        var context  = self.getBindingContext();
        var oModel   = this.getModel();
        oModel.create(self.getCommentPath(), postBody, context, 
          function() {
            self.oReplyInput.setValue('');
            self.setRepliesCount(self.getRepliesCount() + 1);
          }, 
          sap.jam.StringHelper.showErrorMessage
        );
      } else {
        sap.m.MessageBox.alert(sap.jam.StringHelper.getText('post.enter_message_to_share'));
      }
    }); 
  },

  formatText: function() {
    var title = this.getTitle(),
    text = this.getBlurb() || "";

    if(text && text.indexOf(title) < 0) {
      this.setText(title + "\n" + text);  
    } else {  
      this.setText(title);
    }
  },

  bindComments: function(commentPath) {
    var oCommentTemplate = new sap.jam.JamCommentListItem({
      sender: "{Creator/FullName}",
      timestamp: {
        path: "CreatedAt",
        type: new sap.ui.model.type.DateTime({pattern: "yyyy-MM-ddTHH:mm:ssZ"})
      },
      text: "{Text}",
      liked: "{Liked}",
      canDelete: "{CanDelete}",
      iconDensityAware: false,
      creator: '{Creator}',
      icon: {
        path: "ThumbnailImage/__metadata/media_src",
        formatter: function(sUrl) {
          return sUrl ? sap.jam.ProxyHelper.getODataUrl('/' + sUrl, null, this.getModel()) : undefined;
        }
      }      
    });

    this.__getCommentListControl().bindAggregation("items", {
      path: commentPath,
      template: oCommentTemplate,
      parameters: {
        expand: 'Creator,ThumbnailImage'
      }
    });
    this.bCommentsBound = true;
  },

  bindMentionLinks: function() {
    var oLinkTemplate = new sap.jam.MemberLink({
      prefix: '@',
      fullName: '{FullName}',
      title: '{Title}',
      email: '{Email}',
      memberId: '{Id}',
      icon: {
        path: "ThumbnailImage/__metadata/media_src",
        formatter: function(sUrl) {
          return sUrl ? sap.jam.ProxyHelper.getODataUrl('/' + sUrl, null, this.getModel()) : undefined;
        }
      }
    });
    this.bindAggregation('mentionLinks', {
      path: 'AtMentions',
      template: oLinkTemplate,
      parameters: {
        expand: 'ThumbnailImage'
      }
    });
    this.bMentionsBound = true;
  },

  onBeforeRendering: function() {
    var oFooter = this.__getFooterControl(),
      oMoreMenu = this.__getMoreMenuControl();

    if(this.getCommentPath() && !this.bCommentsBound && this.getRepliesCount() > 0) {
      this.bindComments(this.getCommentPath());
    } else if(this.getRepliesCount() === 0) {
      this.__getCommentListControl().unbindAggregation('items');
      this.bCommentsBound = false;
    }

    if(this.getTextWithPlaceholders() && !this.bMentionsBound) {
      this.bindMentionLinks();
    }

    this.formatText();

    if(!this.getCanLike()) {
      oFooter.removeItem(this.oLikeLink);
    }
    if(!this.getCanComment()) {
      oFooter.removeItem(this.oReplyButton);
    }
  },

  onAfterRendering: function() {
    var hasHtml = this.getHasHtml();
    var self = this;

    if (hasHtml) {     // TODO: Clean up once oData API has the HTML content directly.
      var feedId = this.getFeedId();
      var sContentId = this.getId() + '-content';
      jQuery.sap.byId(this.getId()).find('.sapMFeedListItemHtml').append('<div id="' + sContentId + '" class="feed-item-api" style="padding-top:5px"></div>');
      var auth = this.getModel().getHeaders()['Authorization'];
      var obsolete_api_url = this._makeV1UrlFromService('feed/' + feedId + '/html?fixed_height=false&use_full_page=false');

      jQuery.ajax({
        url: obsolete_api_url,
        async: true,
        type: 'GET',
        headers: {'Authorization': auth},
        success: function(data) {
          jQuery('#'+sContentId).html(data);
          self.hasHtml = false;   // hasHtml really means "has html that isn't loaded yet"
        }
      });
    }
    this.addTimeAgo();
  },

  addTimeAgo: function() {
    var time_el = jQuery('#' + this.getId() + ' .timeago');
    if(typeof(time_el.timeago) === 'function') {
      time_el.timeago();
    }
  },

  __getFooterControl: function() {
    return this.getAggregation("_footer");
  },

  __getCommentListControl: function() {
    return this.getAggregation("_comments");
  },

  __getReplyBarControl: function() {
    return this.getAggregation("_replyBar");
  },

  __getMoreMenuControl: function() {
    return this.getAggregation("_moreMenu");
  },

  _makeV1UrlFromService: function(v1Url) {
      // Note: This assumes the OData service is .../api/v1/OData .  
      v1Url = new URI(v1Url);
      return v1Url.absoluteTo( this.getBindingContext().oModel.sServiceUrl+'/../..' ).toString();
  },

  renderer: "sap.jam.JamFeedListItemRenderer"
}); 

sap.jam.JamFeedListItem.extend("sap.jam.JamCommentListItem", {
  init: function() { 
    this.initFooter();
    this.initMoreMenu();
    this.initDeleteButton();
  },

  getHasHtml : function() { return false; },

  onBeforeRendering: function() {
    // var oFooter = this.__getFooterControl(),
    //   oMoreMenu = this.__getMoreMenuControl();

    // if(!this.getCanLike()) {
    //   oFooter.removeItem(this.oLikeLink);
    // }

    if(this.getTextWithPlaceholders() && !this.bMentionsBound) {
      this.bindMentionLinks();
    }    
  },

  onAfterRendering: function() {
    this.addTimeAgo();
  },  

  renderer: "sap.jam.JamFeedListItemRenderer"
}); 

sap.jam.JamFeedListItem.extend("sap.jam.WallCommentListItem", {
  init: function() { 
   this.setAggregation("_comments", new sap.m.List({
      showNoData: false,
      showSeparators: sap.m.ListSeparators.None
    }));

    this.initFooter();
    this.initReplyBox();    
    this.initMoreMenu();     
    this.initDeleteButton();
  },

  renderer: "sap.jam.JamFeedListItemRenderer"
}); 
