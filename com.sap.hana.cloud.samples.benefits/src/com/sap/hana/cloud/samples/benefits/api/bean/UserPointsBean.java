package com.sap.hana.cloud.samples.benefits.api.bean;

import com.google.gson.annotations.Expose;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;

public class UserPointsBean {

    @Expose
    public long campaignId;

    @Expose
    public long availablePoints;

    @Expose
    public String userId;

    public void init(com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints userPoints) {
        this.campaignId = userPoints.getCampaign().getId();
        this.userId = userPoints.getUser().getUserId();
        this.availablePoints = userPoints.getAvailablePoints();
    }

    public static UserPointsBean get(UserPoints userPoints) {
        UserPointsBean result = new UserPointsBean();
        result.init(userPoints);
        return result;
    }

}
