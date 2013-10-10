package com.sap.benefits.management.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sap.benefits.management.persistence.model.DBQueries;
import com.sap.benefits.management.persistence.model.User;

public class UserDAO extends BasicDAO<User> {

	public UserDAO() {
		super();
	}

	public User getByUserId(String userId) {
		final EntityManager em = factory.createEntityManager();
		try {
			final Query query = em.createNamedQuery(DBQueries.GET_USER_BY_USER_ID, User.class);
			query.setParameter("userId", userId);
			final User user = (User) query.getSingleResult();
			return user;
		} catch (javax.persistence.NoResultException x) {
			return null;
		} finally {
			em.close();
		}
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
