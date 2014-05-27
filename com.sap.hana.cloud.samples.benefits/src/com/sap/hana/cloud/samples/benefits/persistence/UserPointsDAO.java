package com.sap.hana.cloud.samples.benefits.persistence;

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
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;


public class UserPointsDAO extends BasicDAO<UserPoints> {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserPointsDAO() {
        super(EntityManagerProvider.getInstance());
    }


    public UserPoints getUserPoints(User user, Campaign campaign) {
        final EntityManager em = emProvider.get();
        TypedQuery<UserPoints> query = em.createNamedQuery(DBQueries.GET_USER_POINTS,UserPoints.class);

        query.setParameter("user", user);
        query.setParameter("campaign", campaign);
        UserPoints result = null;
        try{
        result = query.getSingleResult();
		} catch (NoResultException x) {
			logger.error("Could not retrieve user points for userId {} from table {}.", user.getId(), "User");
		} catch (NonUniqueResultException e) {
			logger.error("More than one entity for userId {} from table {}.", user.getId(), "User");
		}
        return result;
    }
}
