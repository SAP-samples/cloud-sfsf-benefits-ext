package com.sap.hana.cloud.samples.benefits.api.bean;

import java.text.MessageFormat;

import com.google.gson.annotations.Expose;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class UserBean {

    @Expose
    public String firstName;

    @Expose
    public String lastName;

    @Expose
    public String fullName;

    @Expose
    public String userId;

    @Expose
    public String email;

    @Expose
    public UserPointsBean activeCampaignBalance;

    public void init(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = MessageFormat.format("{0} {1}", user.getFirstName(), user.getLastName());
        this.userId = user.getUserId();
    }

    public void setActiveCampaignBalance(com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints userPoints) {
        if (userPoints != null) {
            this.activeCampaignBalance = UserPointsBean.get(userPoints);
        }
    }

    public static UserBean get(User user) {
        UserBean result = new UserBean();
        result.init(user);
        return result;
    }
}
