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

	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public SFUser getUserProfile() throws IOException {
		return CoreODataConnector.getInstance().getUserProfile("nnnn");
	}

	@GET
	@Path("/orders/{campain_id}/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public BenefitsOrderBean getUserBenefitsOrder(@PathParam("campain_id") long campaign_id, @PathParam("user_id") String user_id) {
		BenefitsOrderBean result = new BenefitsOrderBean();
		CampaignDAO campaignDAO = new CampaignDAO();
		Campaign campaign = campaignDAO.getById(campaign_id);
		User user = (new UserDAO()).getByUserId(user_id);
		Collection<Order> orders = (new OrderDAO()).getOrdersForUser(user, campaign);
		if (orders.size() > 0) {
			result.init(orders.iterator().next());
		} else {
			result.campaign = new CampaignBean();
			result.campaign.init(campaign);
		}
		return result;
	}

	@GET
	@Path("/managed")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserBean> getManagedUsers() throws IOException {
		User currentUser = getLoggedInUser();
		CampaignDAO campaignDAO = new CampaignDAO();
		Campaign activeCampaign = campaignDAO.getActiveCampaign(currentUser);
		UserPointsDAO userPontsDAO = new UserPointsDAO();
		List<UserBean> result = new ArrayList<>();
		for (User employee : currentUser.getEmployees()) {
			UserBean newUser = new UserBean();
			newUser.init(employee);
			if (activeCampaign != null) {
				UserPointsPrimaryKey primKey = new UserPointsPrimaryKey(employee.getId(), activeCampaign.getId());
				UserPoints userPointBackend = userPontsDAO.getByPrimaryKey(primKey);
				if (userPointBackend != null) {					
					newUser.setActiveCampaignBalance(userPointBackend);
				}				
			}
			result.add(newUser);
		}

		return result;
	}

	@GET
	@Path("/userCampaigns")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Campaign> getUserCampaigns() throws IOException {
		final User user = getLoggedInUser();
		return user.getHrManager().getCampaigns();
	}

}
