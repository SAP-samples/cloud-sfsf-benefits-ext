package com.sap.benefits.management.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.benefits.management.api.frontend.BenefitsOrderBean;
import com.sap.benefits.management.api.frontend.CampaignBean;
import com.sap.benefits.management.api.frontend.UserBean;
import com.sap.benefits.management.api.frontend.UserPointsBean;
import com.sap.benefits.management.connectivity.CoreODataConnector;
import com.sap.benefits.management.connectivity.helper.SFUser;
import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.OrderDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.UserPointsDAO;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.Order;
import com.sap.benefits.management.persistence.model.User;
import com.sap.benefits.management.persistence.model.UserPoints;
import com.sap.benefits.management.persistence.model.keys.UserPointsPrimaryKey;

@Path("/user")
public class UserService extends BaseService {

	private CampaignDAO campaignDAO = new CampaignDAO();
	private UserPointsDAO userPontsDAO = new UserPointsDAO();

	@GET
	@Path("/orders/{campain_id}/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public BenefitsOrderBean getUserBenefitsOrder(@PathParam("campain_id") long campaign_id, @PathParam("user_id") String user_id) {
		Campaign campaign = campaignDAO.getById(campaign_id);
		User user = (new UserDAO()).getByUserId(user_id);
		Collection<Order> orders = (new OrderDAO()).getOrdersForUser(user, campaign);
		if (orders.size() > 0) {
			return BenefitsOrderBean.get(orders.iterator().next());
		} else {
			return BenefitsOrderBean.getEmpty(campaign);
		}
	}

	@GET
	@Path("/managed")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserBean> getManagedUsers() throws IOException {
		User currentUser = getLoggedInUser();
		Campaign activeCampaign = campaignDAO.getActiveCampaign(currentUser);
		List<UserBean> result = new ArrayList<>();
		for (User employee : currentUser.getEmployees()) {
			UserBean userInfo = UserBean.get(employee);
			if (activeCampaign != null) {
				userInfo.setActiveCampaignBalance(getUserPoints(employee, activeCampaign));
			}
			result.add(userInfo);
		}
		return result;
	}

	private UserPoints getUserPoints(User employee, Campaign campaign) {
		UserPointsPrimaryKey primaryKey = new UserPointsPrimaryKey(employee.getId(), campaign.getId());
		return userPontsDAO.getByPrimaryKey(primaryKey);
	}

	@GET
	@Path("/userCampaigns")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CampaignBean> getUserCampaigns() throws IOException {
		final User user = getLoggedInUser();
		return CampaignBean.getList(user.getHrManager().getCampaigns());
	}

}
