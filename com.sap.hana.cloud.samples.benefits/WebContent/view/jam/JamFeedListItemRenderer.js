jQuery.sap.declare('sap.jam.JamFeedListItemRenderer');
jQuery.sap.require('sap.ui.core.Renderer');
jQuery.sap.require('sap.m.ListItemBaseRenderer');
jQuery.sap.require('sap.m.FeedListItemRenderer');
jQuery.sap.require('sap.ui.commons.RichTooltip');
jQuery.sap.require('sap.ui.commons.FormattedTextView');
jQuery.sap.require('sap.ui.core.format.DateFormat');
jQuery.sap.require('sap.jam.StringHelper');

sap.jam.JamFeedListItemRenderer = sap.ui.core.Renderer.extend(sap.m.FeedListItemRenderer);

sap.jam.JamFeedListItemRenderer.formatterText = sap.ui.core.format.DateFormat.getDateTimeInstance({pattern: "MMMM d, yyyy, HH:mm:ss"})
sap.jam.JamFeedListItemRenderer.formatterIso8691 = sap.ui.core.format.DateFormat.getDateTimeInstance({pattern: "yyyy-MM-ddTHH:mm:ssZ"})

sap.jam.JamFeedListItemRenderer.createHoverCard = function(ctrl, member, icon) {
    var sText = "";
    if (!!member) {
        if (!!member.Title) {
            sText += '<div class="member-title" style="">';
            sText += member.Title;
            sText += '</div>';
        }
        if (!!member.Email) {
            sText += '<div class="member-email" style="">';
            sText += '<div class="email-icon"></div>' ;
            sText += '<a href="mailto:' + member.Email +'">' ;
            sText += member.Email ;
            sText += '</a>';
            sText += '</div>';
        }
    
        var oRichTooltip = new sap.ui.commons.RichTooltip({
            text : sText ,
            title: member.FullName,
            imageSrc : icon,
        });
        ctrl.setTooltip(oRichTooltip);
    }
    return ctrl;
};


sap.jam.JamFeedListItemRenderer.renderLIContent = function(rm, oControl) {
    var m = oControl.getId();
    rm.write('<article');
    rm.writeControlData(oControl);
    rm.addClass('sapMFeedListItem');
    rm.writeClasses();
    rm.write('>');
    if (!!oControl.getShowIcon()) {
        rm.write('<figure id="' + m + '-figure" class ="sapMFeedListItemFigure');
        if (!!oControl.getIcon()) {
            rm.write('">')
        } else {
            rm.write(' sapMFeedListItemIsDefaultIcon">')
        }
        if (!!oControl.getIconActive()) {
            rm.write('<a id="' + m + '-iconRef" ');
            rm.writeAttribute('href', 'javascript:void(0);');
            rm.write('>')
        }
        rm.renderControl(this.createHoverCard(oControl._getImageControl(), oControl.getCreator(), oControl.getIcon()));
        if (!!oControl.getIconActive()) {
            rm.write('</a>')
        }
        rm.write('</figure>')
    }
    rm.write('<div class= "sapMFeedListItemText ');
    if (!!oControl.getShowIcon()) {
        rm.write('sapMFeedListItemHasFigure ')
    }
    rm.write('" >');
    rm.write('<p id="' + m + '-text" class="sapMFeedListItemTextText">');
    if (!!oControl.getSender()) {
        rm.write('<span id="' + m + '-name" class="sapMFeedListItemTextName">');
        var cSenderCtrl = oControl._getLinkControl();
        rm.renderControl(this.createHoverCard(cSenderCtrl, oControl.getCreator(), oControl.getIcon()));
        rm.write(' ');
        rm.write('</span>')
    }
    this.renderLIText(rm,oControl);
    if(oControl.getHasHtml()) {
        rm.write('<div class="sapMFeedListItemHtml"></div>');
    }        
    if (!!oControl.getInfo() || !!oControl.getTimestamp() || !!oControl.getSource()) {
        if (!sap.ui.getCore().getConfiguration().getRTL()) {
            rm.write('<p class="sapMFeedListItemFooter">');
            if (!!oControl.getInfo()) {
                rm.renderControl(this.linkToJamApp(oControl.getInfo(), "/groups/" + oControl.getGroupId(), oControl.getModel()));
                if (!!oControl.getTimestamp() || !!oControl.getSource()) {
                    rm.write('<span>&#160&#160&#x00B7&#160&#160</span>')
                }
            }
            if (!!oControl.getTimestamp()) {
                var oTime = new Date(oControl.getTimestamp());
                rm.write('<span class="timeago" datetime="' + sap.jam.JamFeedListItemRenderer.formatterIso8691.format(oTime) + '">')
                rm.writeEscaped(sap.jam.JamFeedListItemRenderer.formatterText.format(oTime))
                rm.write('</span>');
                rm.write('<span></span>')
                if (!!oControl.getSource()) {
                    rm.write('<span>&#160&#160&#x00B7&#160&#160</span>')
                }                
            }
            if (!!oControl.getSource()) {
                rm.writeEscaped(oControl.getSource())
            }
        } else {
            rm.write('<p class="sapMFeedListItemFooter">');
            if (!!oControl.getSource()) {
                rm.writeEscaped(oControl.getSource())
            }            
            if (!!oControl.getTimestamp()) {
                if (!!oControl.getSource()) {
                    rm.write('<span>&#160&#160&#x00B7&#160&#160</span>')
                }                
                rm.writeEscaped(oControl.getTimestamp())
            }
            if (!!oControl.getInfo()) {
                if (!!oControl.getTimestamp() || !!oControl.getSource()) {
                    rm.write('<span>&#160&#160&#x00B7&#160&#160</span>')
                }
                rm.writeEscaped(oControl.getInfo())
            }
        }
    }
    rm.write('</p>');
    rm.renderControl(oControl.__getFooterControl());
    if(oControl.__getCommentListControl()) {
        rm.renderControl(oControl.__getCommentListControl());
    }
    if(oControl.__getReplyBarControl()) {
        rm.renderControl(oControl.__getReplyBarControl());
    }        
    rm.write('</div>')
    rm.write('</article>')
};

sap.jam.JamFeedListItemRenderer.getSingleUseToken = function(model) {
    //var model = sap.ui.getCore().getModel();
    var auth = model.getHeaders()['Authorization'];
    api_url = new URI("v1/single_use_tokens").absoluteTo( model.sServiceUrl+'/../../../..' ).toString();
    var token;

    jQuery.ajax({
        url: api_url,
        async: false,
        type: 'POST',
        dataType: 'xml',
        headers: {'Authorization': auth},
        success: function(data) {
            token = data.documentElement.id;
        },
    })

    return token;
};

sap.jam.JamFeedListItemRenderer.goToJamApp = function(link_target, model) {
    model = model ? model : sap.ui.getCore().getModel();
    if(!this.jamLocation) {
        var ext = model.oMetadata.oMetadata.extensions;
        for(var i=0; i < ext.length; ++i) {
            if(ext[i].name === "base" && ext[i].namespace === "http://www.w3.org/XML/1998/namespace") {
                var m = ext[i].value.match(new RegExp( 'https?://[^/]+/' ) );
                if (m)
                    this.jamLocation = m[0];
            }
        }
        if (!this.jamLocation) {
            // TODO: can't find the root, so how to continue?
        }
    }
    sut = this.getSingleUseToken(model);
    link_target = URI(link_target).absoluteTo(this.jamLocation).addSearch('single_use_token', sut ).toString();
    window.open(link_target);
};

// link_text is the text to be displayed, link_target is the server-relative path
sap.jam.JamFeedListItemRenderer.linkToJamApp = function(link_text, link_target, model) {
    var link=new sap.m.Link({
        press: function () { sap.jam.JamFeedListItemRenderer.goToJamApp(link_target, model); },
        text: link_text,
        enabled: true,
    });
    return link;
};

sap.jam.JamFeedListItemRenderer.renderLIText = function(rm, oControl) {
    var sText = oControl.getTextWithPlaceholders() || oControl.getText();
    if(sText.length > 600 && !oControl.getTextExpanded()) {
        var s = sText.substr(450);
        var index = s.search(/\s/) + 450;
        var sFirst = sText.substr(0, index-1);
        var sSecond = sText.substr(index);
        var sId = oControl.getId() + '-truncated';
        this.writeText(rm, oControl, sFirst);
        var link = new sap.m.Link({
            press: function() {
                jQuery('#' + sId).show();
                this.destroy();
                oControl.setTextExpanded(true);
            },
            text: ' ' + sap.jam.StringHelper.getText('feed.more_ellipsis')
        });
        rm.renderControl(link);
        rm.write('<span id="' + sId + '" style="display:none;color:inherit;font-weight:inherit;">');
        this.writeText(rm, oControl, sSecond);
        rm.write('</span>');
    } else {
        this.writeText(rm, oControl, sText);
    }
};

sap.jam.JamFeedListItemRenderer.writeText = function(rm, oControl, sText) {
    //output text. If the feed item contains @mentions, replace any @@m{#} placeholders with mention links
    var oLinks = oControl.getAggregation('mentionLinks');
    var startIndex = 0;
    if(oLinks && oLinks.length > 0) {
        var aPlaceholders = sText.match(/@@m\{\d+\}/g);
        aPlaceholders.forEach(function(sPlaceholder) {
            var sUnwritten = sText.substr(startIndex);
            var index = sUnwritten.indexOf(sPlaceholder);
            if(index > -1) {
                rm.writeEscaped(sUnwritten.substr(0, index), true);
                var n = parseInt(sPlaceholder.substr(4,sPlaceholder.length-5));
                if(n < oLinks.length && oLinks[n]) {
                    rm.renderControl(oLinks[n]);
                }
                startIndex += index + sPlaceholder.length;
            }
        });
    }
    rm.writeEscaped(sText.substr(startIndex), true);
};

