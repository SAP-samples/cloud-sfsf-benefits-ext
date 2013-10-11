package com.sap.benefits.management.persistence;

import javax.persistence.EntityManager;

import com.sap.benefits.management.persistence.model.UserPoints;
import com.sap.benefits.management.persistence.model.keys.UserPointsPrimaryKey;

public class UserPointsDAO extends BasicDAO<UserPoints> {

	public UserPointsDAO() {
		super();
	}

	public UserPoints getByPrimaryKey(UserPointsPrimaryKey key) {
		final EntityManager em = factory.createEntityManager();
		try {
			UserPoints points = em.find(UserPoints.class, key);
			if (points != null) {
				em.refresh(points);
			}
			return points;
		} finally {
			em.close();
		}
	}
}
