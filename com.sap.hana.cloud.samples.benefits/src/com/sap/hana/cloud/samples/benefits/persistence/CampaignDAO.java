package com.sap.hana.cloud.samples.benefits.persistence;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.persistence.manager.PersistenceManager;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;
import com.sap.hana.cloud.samples.benefits.persistence.model.keys.UserPointsPrimaryKey;

public class CampaignDAO extends BasicDAO<Campaign> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CampaignDAO() {
        super(PersistenceManager.getInstance().getEntityManagerProvider());
    }

    public Campaign getByName(String name, User user) {
        final EntityManager em = emProvider.get();
        try {
            final TypedQuery<Campaign> query = em.createNamedQuery(DBQueries.GET_CAMPAIGN_BY_NAME, Campaign.class);
            query.setParameter("name", name);
            query.setParameter("owner", user);
            return query.getSingleResult();
        } catch (NoResultException x) {
            logger.error("Could not retrieve entity {} for userId {} from table {}.", name, user.getUserId(), "Campaign");
        } catch (NonUniqueResultException e) {
            logger.error("More than one entity {} for userId {} from table {}.", name, user.getUserId(), "Campaign");
        }

        return null;
    }

    public boolean canBeActive(Long campaignId, User user) {
        final Campaign activeCampaign = getActiveCampaign(user);
        if (activeCampaign != null && !activeCampaign.getId().equals(campaignId)) {
            return false;
        } else {
            return true;
        }
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
        } else {
            return null;
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
