package com.sap.hana.cloud.samples.benefits.persistence;

import javax.persistence.EntityManager;

import com.sap.hana.cloud.samples.benefits.persistence.common.PersistenceManager;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;
import com.sap.hana.cloud.samples.benefits.persistence.model.keys.UserPointsPrimaryKey;

public class UserPointsDAO extends BasicDAO<UserPoints> {

	public UserPointsDAO() {
		super(PersistenceManager.getInstance().getEntityManagerProvider());
	}

	public UserPoints getByPrimaryKey(UserPointsPrimaryKey key) {
		final EntityManager em = emProvider.get();
		UserPoints points = em.find(UserPoints.class, key);
		return points;
	}
}
