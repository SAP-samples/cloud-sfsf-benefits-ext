package com.sap.hana.cloud.samples.benefits.persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.connectivity.CoreODataConnector;
import com.sap.hana.cloud.samples.benefits.odata.beans.BenefitsAmount;
import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;

public class UserPointsDAO extends BasicDAO<UserPoints> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public UserPointsDAO() {
		super(EntityManagerProvider.getInstance());
	}

	public UserPoints getUserPoints(User user, Campaign campaign) {
		return getUserPoints(user.getUserId(), campaign.getId());
	}

	public UserPoints getUserPoints(String userId, long campaignId) {
		final EntityManager em = emProvider.get();
		TypedQuery<UserPoints> query = em.createNamedQuery(DBQueries.GET_USER_POINTS, UserPoints.class);

		query.setParameter("userId", userId); //$NON-NLS-1$
		query.setParameter("campaignId", campaignId); //$NON-NLS-1$
		UserPoints result = null;
		try {
			result = query.getSingleResult();
		} catch (NoResultException x) {
			logger.debug("Could not retrieve user points for userId {} from table {}.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException e) {
			logger.debug("More than one entity for userId {} from table {}.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}

	public void createCampaignUserPoints(Campaign campaign) throws IOException {
		if (campaign.getOwner() != null) {
			List<User> employees = campaign.getOwner().getEmployees();
			List<BenefitsAmount> employeesBenefitsAmount = obtainEmployeesBenefitsAmount(campaign.getOwner().getUserId());
			Map<String, BenefitsAmount> mapping = createBenefitsAmountMapping(employeesBenefitsAmount);
			updateUserPoints(campaign, employees, mapping);
		}
	}

	private List<BenefitsAmount> obtainEmployeesBenefitsAmount(String hrUserId) throws IOException {
		CoreODataConnector oDataConnector = CoreODataConnector.getInstance();
		return oDataConnector.getEmployeesBenefitsAmount(hrUserId);
	}

	private Map<String, BenefitsAmount> createBenefitsAmountMapping(List<BenefitsAmount> employeesBenefitsAmount) {
		Map<String, BenefitsAmount> mapping = new HashMap<>();
		for (BenefitsAmount benefAmnt : employeesBenefitsAmount) {
			mapping.put(benefAmnt.getUserId(), benefAmnt);
		}
		return mapping;
	}

	private void updateUserPoints(Campaign campaign, List<User> employees, Map<String, BenefitsAmount> mapping) {
		for (User employee : employees) {
			BenefitsAmount emplBenefitsAmount = mapping.get(employee.getUserId());
			if (emplBenefitsAmount == null) {
				String errMsg = "Missing benefits amount for the campaign employee " + employee; //$NON-NLS-1$
				logger.error(errMsg);
				throw new IllegalStateException(errMsg);
			}
			saveNewUserPoints(campaign, employee, emplBenefitsAmount.getTargetPoints());
		}

	}

	private void saveNewUserPoints(Campaign campaign, User user, long targetPoints) {
		UserPoints points = getUserPoints(user, campaign);
		if (points == null) {
			points = new UserPoints();
			points.setCampaign(campaign);
			points.setUser(user);
			points.setAvailablePoints(targetPoints);
			points.setEntitlementPoints(targetPoints);
			saveNew(points);
		} else {
			points.setAvailablePoints(targetPoints);
			save(points);
		}

	}
}
