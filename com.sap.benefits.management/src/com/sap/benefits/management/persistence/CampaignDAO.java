package com.sap.benefits.management.persistence;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.DBQueries;
import com.sap.benefits.management.persistence.model.User;

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
			final Campaign campaign = em.getReference(Campaign.class, query.getSingleResult().getId());
			em.refresh(campaign);
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
		}
		return campaign;
	}
	
	public boolean canBeActive(Campaign campaign, User user){
		final Collection<Campaign> activeCampaigns = getActiveCampaigns(user);
		 final Iterator<Campaign> iterator = activeCampaigns.iterator();
		if(!activeCampaigns.isEmpty() && !iterator.next().getId().equals(campaign.getId())){
			return false;
		}
		
		return true;
	}
	
	public Collection<Campaign> getActiveCampaigns(User user){
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<Campaign> query = em.createNamedQuery(DBQueries.GET_ACTIVE_CAMPAIGNS, Campaign.class);
			query.setParameter("owner", user);
			
			return query.getResultList();
		} finally {
			em.close();
		}
	}
}
