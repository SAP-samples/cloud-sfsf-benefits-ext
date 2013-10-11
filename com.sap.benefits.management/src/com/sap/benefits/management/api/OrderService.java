package com.sap.benefits.management.api;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.OrderDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.Order;
import com.sap.benefits.management.persistence.model.User;

@Path("/orders")
public class OrderService extends BaseService{
	
	@GET
	@Path("/ordersForUser/{campName}/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Order> getUserOrders(@PathParam("campName") String campName, @PathParam("userId") String userId) throws IOException {
		UserDAO userDAO = new UserDAO();
		final User user = userDAO.getByUserId(userId);
		
		
		CampaignDAO campaignDAO = new CampaignDAO();
		Campaign campaign = campaignDAO.getByName(campName, user.getHrManager());
		
		OrderDAO orderDAO = new OrderDAO();
		return orderDAO.getOrdersForUser(user, campaign);
	}
	
	@POST
	@Path("/addOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addOrder(Order order){
		UserDAO userDAO = new UserDAO();
		final User user = userDAO.getByUserId(getLoggedInUserId());
		
		OrderDAO orderDAO = new OrderDAO();
		orderDAO.saveOrder(user, order);
		
		return Response.ok().build();	
	}
	

}
