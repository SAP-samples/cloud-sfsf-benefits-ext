package com.sap.hana.cloud.samples.benefits.persistence;

import com.sap.hana.cloud.samples.benefits.persistence.common.PersistenceManager;
import com.sap.hana.cloud.samples.benefits.persistence.model.OrderDetails;

public class OrderDetailDAO extends BasicDAO<OrderDetails>{

	public OrderDetailDAO() {
		super(PersistenceManager.getInstance().getEntityManagerProvider());
	}

}
