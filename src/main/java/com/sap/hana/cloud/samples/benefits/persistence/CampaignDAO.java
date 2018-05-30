package com.sap.hana.cloud.samples.benefits.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

@SuppressWarnings("nls")
public class CampaignDAO extends BasicDAO<Campaign> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public CampaignDAO() {
		super(EntityManagerProvider.getInstance());
	}

	public Campaign getByCaseInsensitiveName(String name, User user) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<Campaign> query = em.createNamedQuery(DBQueries.GET_CAMPAIGN_BY_CASE_INSENSITIVE_NAME, Campaign.class);
			query.setParameter("name", name);
			query.setParameter("owner", user);
			return query.getSingleResult();
		} catch (NoResultException x) {
			logger.warn("Could not retrieve entity {} for userId {} from table {}.  Maybe the user doesn't exist yet.", name, user.getUserId(),
					"Campaign");
		} catch (NonUniqueResultException e) {
			throw new IllegalStateException(String.format(
					"More than one campaign with name %s for userId %s from table Campaign.", name, user.getUserId())); //$NON-NLS-1$
		}

		return null;
	}

	public void delete(long id) {
		final EntityManager em = emProvider.get();
		final Campaign campaign = em.find(Campaign.class, id);
		em.getTransaction().begin();
		if (campaign != null) {
			campaign.getOwner().getCampaigns().remove(campaign);
			em.merge(campaign.getOwner());
			em.remove(campaign);
		}
		em.getTransaction().commit();
	}

	public Campaign getActiveCampaign(User owner) {
		final EntityManager em = emProvider.get();
		final TypedQuery<Campaign> query = em.createNamedQuery(DBQueries.GET_ACTIVE_CAMPAIGNS, Campaign.class);
		query.setParameter("owner", owner);
		List<Campaign> result = query.getResultList();
		if (result.size() == 1) {
			return result.get(0);
		}
		return null;
	}

}
