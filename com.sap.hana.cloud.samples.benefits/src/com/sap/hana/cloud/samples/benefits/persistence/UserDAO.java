package com.sap.hana.cloud.samples.benefits.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class UserDAO extends BasicDAO<User> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public UserDAO() {
		super(EntityManagerProvider.getInstance());
	}

	public User getByUserId(String userId) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<User> query = em.createNamedQuery(DBQueries.GET_USER_BY_USER_ID, User.class);
			query.setParameter("userId", userId); //$NON-NLS-1$
			User user = query.getSingleResult();
			return user;
		} catch (NoResultException x) {
			logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException e) {
			logger.error("More than one entity for userId {} from table {}.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return null;
	}

	public User getOrCreateUser(String userId) {
		User user = getByUserId(userId);
		if (user == null) {
			user = new User();
			user.setUserId(userId);
			saveNew(user);
		}
		return user;
	}

}
