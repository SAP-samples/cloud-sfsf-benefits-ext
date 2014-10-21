jQuery.sap.require('sap.jam.ProxyHelper');
jQuery.sap.require('sap.jam.StringHelper');

sap.m.TextArea.extend("sap.jam.AutocompleteTextArea", {
  metadata : {
    properties : {
      "prefixIndex": "int"
    },

    aggregations : {
      "popover": {type: "sap.m.Popover", multiple: false}
    }
  },

  init: function() {
    sap.m.TextArea.prototype.init.call(this);

    var self = this;
    this._list = new sap.m.List({
      width: '100%',
      showNoData: false,
      mode: sap.m.ListMode.SingleSelectMaster,
    });

    this.setAggregation("popover", new sap.m.Popover({
      width: '100%',
      placement: sap.m.PlacementType.Vertical,
      showHeader: false,
      content: [
        this._list
      ],
      initialFocus: this
    }));
    this.getPopover().addStyleClass('sapMInputSuggestionPopup');

    this.attachLiveChange(this.onLiveChange);
  },  

  onLiveChange: function(oEvent) {
    var self = this,
      newText = oEvent.getParameters().newValue,
      cursor = this.getFocusInfo().cursorPos;
    var s = newText.substr(0,cursor);
    var wordStart = Math.max(s.lastIndexOf(' '), s.lastIndexOf('\n')) + 1;
    var wordEnd = s.search(/\s/);
    if (wordEnd === -1) {
      wordEnd = undefined;
    } else {
      wordEnd += cursor;
    }
    var word = newText.substring(wordStart,wordEnd);
    this._lastQuerySegment = word.substr(1);
    if(word[0] === '@' && word.length > 2) {
      this.setPrefixIndex(wordStart);
      that = this; //as setTimeout changes the context (this)
      setTimeout(function(){that.showMentions(word.substr(1))},375);
    } else if(this.getPopover().isOpen()) {
      this.getPopover().close();
    }
  },

  showMentions: function(sWord) {
    if (sWord != this._lastQuerySegment) {
      return;
    }
    var self = this,
      oModel = this.getModel(),
      params = {Query: "'" + sWord + "'"};
    
    var groupId = this.getGroupId(oModel);
    if(groupId) {
      params.GroupId = "'" + groupId + "'";
    }

    oModel.read('/Members_Autocomplete', {
      urlParameters: params,
      success: function(oData, response) {
        if (sWord != self._lastQuerySegment){
          return;
        }
        var oList = self._list;
        oList.destroyItems();
        if(oData && oData.results) {
          oData.results.forEach(function(result) {
            oList.addItem(new sap.m.StandardListItem({
              title: result.FullName,
              description: result.Email,
              icon: sap.jam.ProxyHelper.getODataUrl("/Members('" + result.Id + "')/ThumbnailImage/$value", null, oModel),
              iconDensityAware: false
            }));
          });
        }
        oList.addItem(new sap.m.StandardListItem({
          title: '@notify ' + sap.jam.StringHelper.getText('common.sends_email_to_all')         
        }));
        oList.getItems()[0].setSelected(true);

        if(!self.getPopover().isOpen()) {
          self.getPopover().openBy(self);
        }        
      },
      error: function(response) {

      }
    });    
  },

  onsapup: function(oEvent) {
    this._onsaparrowkey(oEvent, 'up');
  },

  onsapdown: function(oEvent) {
    this._onsaparrowkey(oEvent, 'down');
  },

  onsapescape: function(oEvent) {
    if (this.getPopover().isOpen()) {
      this.getPopover().close();
      return;
    }
    if (sap.m.InputBase.prototype.onsapescape) {
      sap.m.InputBase.prototype.onsapescape.apply(this, arguments)
    }
  },

  onsapenter: function(oEvent) {
    if (this.getPopover().isOpen()) {
      this.itemSelected(this._list.getSelectedItem());
      oEvent.preventDefault();
      oEvent.stopPropagation();
      return;
    }
    if (sap.m.InputBase.prototype.onsapenter) {
      sap.m.InputBase.prototype.onsapenter.apply(this, arguments)
    }
  },

  onsapfocusleave: function(oEvent) {
    if (oEvent.relatedControlId && jQuery.sap.containsOrEquals(this.getPopover().getFocusDomRef(), sap.ui.getCore().byId(oEvent.relatedControlId).getFocusDomRef())) {
      this._list.attachSelectionChange(this.selectionChange, this);
      this.focus();
    } else if (this.getPopover().isOpen()) {
      this.getPopover().close();
    }
  },

  _onsaparrowkey: function(oEvent, sDirection) {
    if(!this.getPopover().isOpen()) {
      return;
    }
    var selectedIndex = this._list.indexOfItem(this._list.getSelectedItem());
    if(sDirection === 'up' && selectedIndex > 0) {
      selectedIndex--;
    } else if(sDirection === 'down' && selectedIndex < this._list.getItems().length-1) {
      selectedIndex++;
    }
    this._list.getItems()[selectedIndex].setSelected(true);
    oEvent.preventDefault();
    oEvent.stopPropagation();
  },

  selectionChange: function(oEvent) {
    this.itemSelected(oEvent.getParameters().listItem);
  },

  itemSelected: function(oItem) {
    this._list.detachSelectionChange(this.selectionChange, this);
    oItem = oItem || this._list.getSelectedItem();
    if (this.getPopover().isOpen()) {
      var sValue = this.getValue(),
        prefixIndex = this.getPrefixIndex(),
        sHandle = this.getMemberHandle(oItem);

      var sNewValue = sValue.substr(0, prefixIndex) + sHandle + sValue.substr(this.getFocusInfo().cursorPos);
      this.setValue(sNewValue);

      this.getPopover().close();
    }  
  },

  getMemberHandle: function(oItem) {
    var sHandle = oItem.getDescription();
    if(sHandle) {
      sHandle = '@' + sHandle.split('@').shift();
    } else if(oItem.getTitle().indexOf('@notify') > -1) {
      sHandle = '@@notify';
    }
    return sHandle;
  },

  getGroupId: function(oModel) {
    if(this._groupId) {
      return this._groupId;
    }

    var oContext = this.getBindingContext();
    if(oContext) {
      var sPath;
      if(oContext.getPath().indexOf('Groups') > -1) {
        sPath = 'Id';
      } else if(oContext.getPath().indexOf('FeedEntries') > -1) {
        sPath = 'Group/Id';
      }
      if(sPath) {
        return oModel.getProperty(sPath,oContext);
      }
    }
    return undefined;
  },

  renderer: {}
}); 

