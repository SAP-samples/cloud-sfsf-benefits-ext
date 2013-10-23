package com.sap.hana.cloud.samples.benefits.persistence;

import javax.persistence.EntityManager;

import com.sap.hana.cloud.samples.benefits.persistence.manager.PersistenceManager;
import com.sap.hana.cloud.samples.benefits.persistence.model.OrderDetails;

public class OrderDetailDAO extends BasicDAO<OrderDetails>{

	public OrderDetailDAO() {
		super(PersistenceManager.getInstance().getEntityManagerProvider());
	}
	
	public void delete(long id) {
		final EntityManager em = emProvider.get();
		final OrderDetails orderDetail = em.find(OrderDetails.class, id);
		em.getTransaction().begin();
		if (orderDetail != null) {
			orderDetail.getOrder().getOrderDetails().remove(orderDetail);
			em.remove(orderDetail);
		}
		em.getTransaction().commit();
	}	

}
