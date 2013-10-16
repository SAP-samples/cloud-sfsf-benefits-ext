package com.sap.hana.cloud.samples.benefits.persistence;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;
import com.sap.hana.cloud.samples.benefits.persistence.model.keys.UserPointsPrimaryKey;

public class CampaignDAO extends BasicDAO<Campaign> {

	public CampaignDAO() {
		super();
	}

	public Campaign getByName(String name, User user) {
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<Campaign> query = em.createNamedQuery(DBQueries.GET_CAMPAIGN_BY_NAME, Campaign.class);
			query.setParameter("name", name);
			query.setParameter("owner", user);
			final Campaign campaign = em.find(Campaign.class, query.getSingleResult().getId());
			if (campaign != null) {
				em.refresh(campaign);
			}
			return campaign;
		} catch (javax.persistence.NoResultException x) {
			return null;
		} finally {
			em.close();
		}
	}

	public Campaign getOrCreateCampaign(String name, User user) {
		Campaign campaign = getByName(name, user);
		if (campaign == null) {
			campaign = new Campaign();
			campaign.setName(name);
			campaign.setOwner(user);
			saveNew(campaign);

			setPointsToUsers(campaign);
		}
		return campaign;
	}

	public boolean canBeActive(Campaign campaign, User user) {
		final Campaign activeCampaign = getActiveCampaign(user);
		if (activeCampaign != null && !activeCampaign.getId().equals(campaign.getId())) {
			return false;
		} else {
			return true;
		}
	}

	public Campaign getActiveCampaign(User user) {
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<Campaign> query = em.createNamedQuery(DBQueries.GET_ACTIVE_CAMPAIGNS, Campaign.class);
			query.setParameter("owner", user);
			List<Campaign> result = query.getResultList();
			if (result.size() == 1) {
				return result.get(0);
			} else {
				return null;
			}
		} finally {
			em.close();
		}
	}

	public void setPointsToUsers(Campaign campaign) {
		final UserPointsDAO userPointsDAO = new UserPointsDAO();
		if (campaign.getOwner() != null) {
			final Collection<User> employees = campaign.getOwner().getEmployees();
			for (User user : employees) {
				UserPoints points = userPointsDAO.getByPrimaryKey(new UserPointsPrimaryKey(user.getId(), campaign.getId()));
				if (points == null) {
					points = new UserPoints();
					points.setCampaign(campaign);
					points.setUser(user);
					points.setAvailablePoints(campaign.getPoints());
					userPointsDAO.saveNew(points);
				}
			}
		}
	}
}
