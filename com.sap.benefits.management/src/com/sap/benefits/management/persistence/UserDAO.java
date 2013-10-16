package com.sap.benefits.management.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import com.sap.benefits.management.persistence.model.DBQueries;
import com.sap.benefits.management.persistence.model.User;

public class UserDAO extends BasicDAO<User> {

	public UserDAO() {
		super();
	}

	public User getByUserId(String userId) {
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<Long> query = em.createNamedQuery(DBQueries.GET_USER_PK_BY_USER_ID, Long.class);
			query.setParameter("userId", userId);
			final Long userPrimaryKey = query.getSingleResult();
			if (userPrimaryKey != null) {
				User user = em.find(User.class, userPrimaryKey);
				em.refresh(user);
				return user;
			}
			return null;
		} catch (NoResultException | NonUniqueResultException x) {
			return null;
		}finally {
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
