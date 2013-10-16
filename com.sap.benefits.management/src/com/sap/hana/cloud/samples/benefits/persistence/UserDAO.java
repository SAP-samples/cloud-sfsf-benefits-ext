package com.sap.hana.cloud.samples.benefits.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class UserDAO extends BasicDAO<User> {

	public UserDAO() {
		super();
	}

	public User getByUserId(String userId) {
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<User> query = em.createNamedQuery(DBQueries.GET_USER_PK_BY_USER_ID, User.class);
			query.setParameter("userId", userId);
			final User user = query.getSingleResult();
//			if (userPrimaryKey != null) {
//				User user = em.find(User.class, userPrimaryKey);
//				em.refresh(user);
//				return user;
//			}
			return user;
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
