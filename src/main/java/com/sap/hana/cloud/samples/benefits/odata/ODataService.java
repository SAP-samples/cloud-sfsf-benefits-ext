package com.sap.hana.cloud.samples.benefits.odata;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.processor.ODataContext;

import com.sap.hana.cloud.samples.benefits.odata.cfg.BenefitsODataServiceFactory;
import com.sap.hana.cloud.samples.benefits.persistence.CampaignDAO;
import com.sap.hana.cloud.samples.benefits.persistence.UserDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.service.SessionCreateFilter;

public abstract class ODataService {

	protected UserDAO userDAO;
	protected CampaignDAO campaignDAO;

	public ODataService() {
		this.userDAO = new UserDAO();
		this.campaignDAO = new CampaignDAO();
	}

	protected User getLoggedInSfUser() {
		return userDAO.getByUserId(getSFUser());
	}

	private String getSFUser() {
		ODataContext ctx = BenefitsODataServiceFactory.getContextInThreadLocal();
		HttpServletRequest httpServlReq = (HttpServletRequest) ctx.getParameter(ODataContext.HTTP_SERVLET_REQUEST_OBJECT);
		return (String) httpServlReq.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
	}
}
