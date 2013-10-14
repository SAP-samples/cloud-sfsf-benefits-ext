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

import com.sap.benefits.management.api.frontend.OrderBean;
import com.sap.benefits.management.persistence.BenefitTypeDAO;
import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.OrderDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.model.BenefitType;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.Order;
import com.sap.benefits.management.persistence.model.OrderDetails;
import com.sap.benefits.management.persistence.model.User;

@Path("/orders")
public class OrderService extends BaseService {

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

	@GET
	@Path("/ordersForUser/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Order> getUserAllOrders(@PathParam("userId") String userId) throws IOException {
		UserDAO userDAO = new UserDAO();
		final User user = userDAO.getByUserId(userId);

		OrderDAO orderDAO = new OrderDAO();
		return orderDAO.getAllOrdersForUser(user);
	}

	@POST
	@Path("/add/{campaignId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addOrder(@PathParam("campaignId") long campaignId, OrderBean request) {
		final User user = getLoggedInUser();
		final Campaign campaign = new CampaignDAO().getById(campaignId);

		if (campaign == null) {
			return createBadRequestResponse("Incorrect campaign id");
		}

		final OrderDAO orderDAO = new OrderDAO();
		final Order userOrder = getOrCreateUserOrder(user, campaign, orderDAO);
		final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
		final BenefitType benefitType = benefitTypeDAO.getById(request.benefitTypeId);
		if (benefitType == null) {
			return createBadRequestResponse("Incorrect benefit type id");
		}
		final OrderDetails orderDetails = createOrderDetails(request, benefitType);

		userOrder.addOrderDetails(orderDetails);
		orderDAO.save(userOrder);

		return createOkResponse();
	}

	private Order getOrCreateUserOrder(final User user, final Campaign campaign, final OrderDAO orderDAO) {
		Order userOrder = null;
		final Collection<Order> ordersOfUser = orderDAO.getOrdersForUser(user, campaign);
		if (ordersOfUser.isEmpty()) {
			userOrder = orderDAO.createOrderForUser(user, campaign);
		} else {
			userOrder = ordersOfUser.iterator().next();
		}
		return userOrder;
	}

	private OrderDetails createOrderDetails(OrderBean request, final BenefitType benefitType) {
		final OrderDetails orderDetails = new OrderDetails();
		orderDetails.setBenefitType(benefitType);
		orderDetails.setQuantity(request.quantity);
		return orderDetails;
	}

}
