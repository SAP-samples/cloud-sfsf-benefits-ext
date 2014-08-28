package com.sap.hana.cloud.samples.benefits.odata;

import com.sap.hana.cloud.samples.benefits.persistence.CampaignDAO;
import com.sap.hana.cloud.samples.benefits.persistence.UserDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public abstract class ODataService {

	protected UserDAO userDAO;
	protected CampaignDAO campaignDAO;

	public ODataService() {
		this.userDAO = new UserDAO();
		this.campaignDAO = new CampaignDAO();
	}

	protected User getLoggedInUser() {
		return userDAO.getByUserId(UserManager.getUserId());
	}
}
