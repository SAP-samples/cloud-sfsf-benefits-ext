package com.sap.hana.cloud.samples.benefits.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries;
import com.sap.hana.cloud.samples.benefits.persistence.model.Order;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class OrderDAO extends BasicDAO<Order> {

	public OrderDAO() {
		super();
	}
	
	public Order createOrderForUser(User user, Campaign campaign){
		final Order order = new Order();
		order.setUser(user);
		order.setCampaign(campaign);
		
		saveNew(order);
		return order;
	}
	
	public Collection<Order> getOrdersForUser(User user, Campaign campaign) {
		final List<Order> result = new ArrayList<>();
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<Order> query = em.createNamedQuery(DBQueries.GET_USER_ORDERS_FOR_CAMPAIGN, Order.class);
			query.setParameter("user", user);
			query.setParameter("campaign", campaign);
			for (Order order : query.getResultList()) {
				final Order managedOrder = em.find(Order.class, order.getId());
				em.refresh(managedOrder);
				result.add(managedOrder);
			}

			return result;
		} finally {
			em.close();
		}
	}
	
	public Collection<Order> getAllOrdersForUser(User user) {
		final List<Order> result = new ArrayList<>();
		final EntityManager em = factory.createEntityManager();
		try {
			final TypedQuery<Order> query = em.createNamedQuery(DBQueries.GET_USER_ALL_ORDERS, Order.class);
			query.setParameter("user", user);
			for (Order order : query.getResultList()) {
				final Order managedOrder = em.find(Order.class, order.getId());
				em.refresh(managedOrder);
				result.add(managedOrder);
			}

			return result;
		} finally {
			em.close();
		}
	}
	
	public void saveOrder(User user, Order order){
		if(order != null){
			order.setUser(user);
			saveNew(order);
		}
	}

}
