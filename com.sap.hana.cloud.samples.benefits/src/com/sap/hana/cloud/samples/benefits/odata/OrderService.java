package com.sap.hana.cloud.samples.benefits.odata;

import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.BENEFIT_TYPE_ID;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.CAMPAIGN_ID;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.ORDER_ID;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.QUANTITY;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.USER_ID;
import static org.apache.olingo.odata2.api.annotation.edm.EdmType.INT64;
import static org.apache.olingo.odata2.api.annotation.edm.EdmType.STRING;

import java.util.Collection;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.HttpMethod;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitTypeDAO;
import com.sap.hana.cloud.samples.benefits.persistence.OrderDAO;
import com.sap.hana.cloud.samples.benefits.persistence.OrderDetailDAO;
import com.sap.hana.cloud.samples.benefits.persistence.UserPointsDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.Order;
import com.sap.hana.cloud.samples.benefits.persistence.model.OrderDetails;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;

public class OrderService extends ODataService {
	private static final String ORDER_DETAIL_NOT_VALID_MESSAGE = "The order value exceedes the available unused points, so is not valid. The order is not persisted"; //$NON-NLS-1$
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private UserPointsDAO userPointsDAO = new UserPointsDAO();

	@EdmFunctionImport(name = FunctionImportNames.ADD_ORDER, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean addOrder(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = INT64) Long campaignId,
			@EdmFunctionImportParameter(name = USER_ID, type = STRING) String userId,
			@EdmFunctionImportParameter(name = QUANTITY, type = INT64) Long quantity,
			@EdmFunctionImportParameter(name = BENEFIT_TYPE_ID, type = INT64) Long benefitTypeId) throws AppODataException {
		final User loggedInUser = getLoggedInSfUser();
		if (!(loggedInUser.getUserId().equals(userId) || UserManager.getIsUserAdmin())) {
			throw new AppODataException("Unauthorized"); //$NON-NLS-1$
		}
		final User user = userDAO.getByUserId(userId);
		final Campaign campaign = campaignDAO.getById(campaignId);

		if (campaign == null) {
			throw new AppODataException("Incorrect campaign id"); //$NON-NLS-1$
		}
		if (!campaign.getActive()) {
			throw new AppODataException("The campaign with id " + campaignId + " is not active"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		final OrderDAO orderDAO = new OrderDAO();
		final Order userOrder = getOrCreateUserOrder(user, campaign, orderDAO);
		final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
		final BenefitType benefitType = benefitTypeDAO.getById(benefitTypeId);
		if (benefitType == null) {
			throw new AppODataException("Incorrect benefit type id"); //$NON-NLS-1$
		}
		final OrderDetails orderDetails = createOrderDetails(quantity, benefitType);
		final UserPoints userPoints = getUserPoints(userOrder);
		final long orderDetailsTotal = calcPointsToAdd(orderDetails);

		if (userPoints.getAvailablePoints() < orderDetailsTotal) {
			throw new AppODataException(ORDER_DETAIL_NOT_VALID_MESSAGE);
		}

		userOrder.addOrderDetails(orderDetails);
		new OrderDetailDAO().saveNew(orderDetails);
		userPoints.subtractPoints(orderDetailsTotal);
		userPointsDAO.save(userPoints);
		return true;
	}

	@EdmFunctionImport(name = FunctionImportNames.DELETE_ORDER, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.DELETE)
	public boolean deleteOrderDetail(@EdmFunctionImportParameter(name = ORDER_ID, type = INT64) Long orderId) throws AppODataException {
		final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
		final Order order = orderDetailDAO.getOrderByOrderDetailsId(orderId);
		final OrderDetails details = orderDetailDAO.getById(orderId);
		final UserPoints userPoints = getUserPoints(order);
		try {
			order.removeOrderDetails(details);
			userPoints.addPoints(calcPointsToAdd(details));
			userPointsDAO.save(userPoints);
			orderDetailDAO.delete(orderId);
			return true;
		} catch (IllegalArgumentException ex) {
			logger.error("Error occur while deleting order with id:{}", orderId, ex); //$NON-NLS-1$
			throw new AppODataException("Error occur while deleting order", ex); //$NON-NLS-1$
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

	private OrderDetails createOrderDetails(Long quantity, BenefitType benefitType) {
		final OrderDetails orderDetails = new OrderDetails();
		orderDetails.setBenefitType(benefitType);
		orderDetails.setQuantity(quantity);
		return orderDetails;
	}

	private UserPoints getUserPoints(Order order) {
		return userPointsDAO.getUserPoints(order.getUser(), order.getCampaign());
	}

	private long calcPointsToAdd(OrderDetails orderDetail) {
		return orderDetail.getQuantity() * orderDetail.getBenefitType().getValue();
	}

}
