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

import com.sap.benefits.management.api.frontend.User;
import com.sap.benefits.management.api.frontend.UserPoints;
import com.sap.benefits.management.connectivity.CoreODataConnector;
import com.sap.benefits.management.connectivity.helper.SFUser;
import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.OrderDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.UserPointsDAO;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.Order;
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
	public Collection<Order> getUserOrders(@PathParam("id") long campaign_id, @PathParam("id") String user_id) {
		CampaignDAO campaignDAO = new CampaignDAO();
		Campaign campaign = campaignDAO.getById(campaign_id);
		com.sap.benefits.management.persistence.model.User user = (new UserDAO()).getByUserId(user_id);
		Collection<Order> orders = (new OrderDAO()).getOrdersForUser(user, campaign);
		return orders;
	}
	
	
	@GET
	@Path("/managed")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getManagedUsers() throws IOException {
		UserDAO userDAO = new UserDAO();
		com.sap.benefits.management.persistence.model.User currentUser = userDAO.getByUserId(getLoggedInUserId());
		CampaignDAO campaignDAO = new CampaignDAO();
		Campaign activeCampaign = campaignDAO.getActiveCampaign(currentUser);
		UserPointsDAO userPontsDAO = new UserPointsDAO();
		List<User> result = new ArrayList<>();
		for (com.sap.benefits.management.persistence.model.User employee: currentUser.getEmployees()) {
			User newUser = new User(employee);
			if (activeCampaign != null) {
				UserPointsPrimaryKey primKey = new UserPointsPrimaryKey(employee.getId(), activeCampaign.getId());
				com.sap.benefits.management.persistence.model.UserPoints userPontBackend = userPontsDAO.getByPrimaryKey(primKey);
				UserPoints userPoints = new UserPoints();
				userPoints.setAvailablePoints(userPontBackend.getAvailablePoints());
				userPoints.setCampaignName(activeCampaign.getName());
				userPoints.setUserId(employee.getUserId());
				newUser.setActiveCampaignBalance(userPoints);
			}			
			result.add(newUser);
		}
		
		return result;		
	}
	
	

}
