sap.m.Link.extend("sap.jam.MemberLink", {
  metadata: {
    properties: {
      'prefix': 'string',
      'fullName': 'string',
      'title': 'string',
      'email': 'string',
      'memberId': 'string',
      'icon': 'string'
    }
  },

  init: function() {
    this.attachPress(this.onPress);
  },

  onPress: function(oEvent){
    if(this.getMemberId()) {
      sap.jam.JamFeedListItemRenderer.goToJamApp("/profile/wall/" + this.getMemberId(), this.getModel());
    }
  },

  getMember: function() {
    var oMember = {
      Title: this.getTitle(),
      Email: this.getEmail(),
      FullName: this.getFullName()
    }
    return oMember;
  },

  onBeforeRendering: function() {
    var sPrefix = this.getPrefix() || '';
    this.setText(sPrefix + this.getFullName());
    sap.jam.JamFeedListItemRenderer.createHoverCard(this, this.getMember(), this.getIcon());
  },

  renderer: {
  }
});