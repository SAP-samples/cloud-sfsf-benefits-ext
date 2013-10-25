package com.sap.hana.cloud.samples.benefits.api;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.api.bean.BenefitsOrderBean;
import com.sap.hana.cloud.samples.benefits.api.bean.OrderBean;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitTypeDAO;
import com.sap.hana.cloud.samples.benefits.persistence.CampaignDAO;
import com.sap.hana.cloud.samples.benefits.persistence.OrderDAO;
import com.sap.hana.cloud.samples.benefits.persistence.OrderDetailDAO;
import com.sap.hana.cloud.samples.benefits.persistence.UserDAO;
import com.sap.hana.cloud.samples.benefits.persistence.UserPointsDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.Order;
import com.sap.hana.cloud.samples.benefits.persistence.model.OrderDetails;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;
import com.sap.hana.cloud.samples.benefits.persistence.model.keys.UserPointsPrimaryKey;

@Path("/orders")
public class OrderService extends BaseService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String ORDER_DETAIL_NOT_VALID_MESSAGE = "The order value exceedes the available unused points, so is not valid. The order is not persisted";
	private CampaignDAO campaignDAO = new CampaignDAO();
	private UserPointsDAO userPointsDAO = new UserPointsDAO();

	@GET
	@Path("/for-user/{campain_id}/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public BenefitsOrderBean getUserBenefitsOrder(@PathParam("campain_id") long campaign_id, @PathParam("user_id") String user_id) throws IOException {
		final User loggedInUser = getLoggedInUser();
		if (loggedInUser.getUserId().equals(user_id) || request.isUserInRole(ADMIN_ROLE)) {
			Campaign campaign = campaignDAO.getById(campaign_id);
			User user = (new UserDAO()).getByUserId(user_id);
			Collection<Order> orders = (new OrderDAO()).getOrdersForUser(user, campaign);
			if (orders.size() > 0) {
				return BenefitsOrderBean.get(orders.iterator().next());
			} else {
				return BenefitsOrderBean.getEmpty(campaign);
			}
		} else {
			response.sendError(403);
		}
		return null;
	}

	@POST
	@Path("/add/{campaignId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addOrder(@PathParam("campaignId") long campaignId, OrderBean request) throws IOException {
		return this.addOrder(campaignId, getLoggedInUserId(), request);
	}

	@POST
	@Path("/add/{campaignId}/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addOrder(@PathParam("campaignId") long campaignId, @PathParam("userId") String userId, OrderBean requestData) throws IOException {
		final User loggedInUser = getLoggedInUser();
		if (loggedInUser.getUserId().equals(userId) || request.isUserInRole(ADMIN_ROLE)) {
			final User user = userDAO.getByUserId(userId);
			final Campaign campaign = campaignDAO.getById(campaignId);

			if (campaign == null) {
				return createBadRequestResponse("Incorrect campaign id");
			}

			final OrderDAO orderDAO = new OrderDAO();
			final Order userOrder = getOrCreateUserOrder(user, campaign, orderDAO);
			final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
			final BenefitType benefitType = benefitTypeDAO.getById(requestData.benefitTypeId);
			if (benefitType == null) {
				return createBadRequestResponse("Incorrect benefit type id");
			}
			final OrderDetails orderDetails = createOrderDetails(requestData, benefitType);
			final UserPoints userPoints = getUserPoints(userOrder);
			final long orderDetailsTotal = calcPointsToAdd(orderDetails);
			if (userPoints.getAvailablePoints() >= orderDetailsTotal){
				userOrder.addOrderDetails(orderDetails);
				new OrderDetailDAO().saveNew(orderDetails);
				userPoints.subtractPoints(orderDetailsTotal);
				userPointsDAO.save(userPoints);
				return createOkResponse();
			} else {
				return createBadRequestResponse(ORDER_DETAIL_NOT_VALID_MESSAGE);	
			}	
		} else {
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public void deleteOrderDetail(@PathParam("id") long orderDetailId) throws IOException {
		final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
		Order order = orderDetailDAO.getOrderByOrderDetailsId(orderDetailId);
		OrderDetails details = orderDetailDAO.getById(orderDetailId);
		final UserPoints userPoints = getUserPoints(order);
		try {
			order.removeOrderDetails(details);
			userPoints.addPoints(calcPointsToAdd(details));
			userPointsDAO.save(userPoints);
			orderDetailDAO.delete(orderDetailId);
		} catch (IllegalArgumentException ex) {
			logger.error("Error occur while deleting order with id:{}", orderDetailId, ex);
			response.sendError(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
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
	
	private UserPoints getUserPoints(Order order){
		final UserPointsPrimaryKey primaryKey = new UserPointsPrimaryKey(order.getUser().getId(), order.getCampaign().getId());
		return userPointsDAO.getByPrimaryKey(primaryKey);
	}
	private long calcPointsToAdd(OrderDetails orderDetail){
		return orderDetail.getQuantity() * orderDetail.getBenefitType().getValue();
	}

}
