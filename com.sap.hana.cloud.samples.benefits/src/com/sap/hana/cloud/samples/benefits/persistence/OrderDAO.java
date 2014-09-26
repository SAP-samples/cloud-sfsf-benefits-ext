package com.sap.hana.cloud.samples.benefits.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.Order;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class OrderDAO extends BasicDAO<Order> {

	public OrderDAO() {
		super(EntityManagerProvider.getInstance());
	}

	public Order createOrderForUser(User user, Campaign campaign) {
		final Order order = new Order();
		order.setUser(user);
		order.setCampaign(campaign);

		saveNew(order);
		return order;
	}

	public Collection<Order> getOrdersForUser(User user, Campaign campaign) {
		final EntityManager em = emProvider.get();
		final TypedQuery<Order> query = em.createNamedQuery(DBQueries.GET_USER_ORDERS_FOR_CAMPAIGN, Order.class);
		query.setParameter("user", user); //$NON-NLS-1$
		query.setParameter("campaign", campaign); //$NON-NLS-1$

		return query.getResultList();
	}

}
