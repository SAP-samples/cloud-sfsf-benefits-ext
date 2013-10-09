package com.sap.benefits.management.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.sap.benefits.management.persistence.util.DataSourceProvider;
import com.sap.benefits.management.persistence.util.EntityManagerFactoryProvider;

public class BasicDAO<T> {
	protected EntityManagerFactory factory;

	public BasicDAO() throws NamingException {
		this.factory = EntityManagerFactoryProvider.getInstance().createEntityManagerFactory(DataSourceProvider.getInstance().getDefault());
	}

	public T save(T entity) {
		final EntityManager em = factory.createEntityManager();
		try {
			em.getTransaction().begin();

			final T merge = em.merge(entity);

			em.getTransaction().commit();
			return merge;
		} finally {
			em.close();
		}

	}
	
	public T saveNew(T entity) {
		final EntityManager em = factory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(entity);

			em.getTransaction().commit();
			return entity;
		} finally {
			em.close();
		}
	}

	public void deleteAll() {
		final EntityManager em = factory.createEntityManager();
		try {
			em.getTransaction().begin();

			Query query = em.createQuery("delete from " + getTableName() + " t ");
			query.executeUpdate();

			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	private Type getActualType() {
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) genericSuperclass;
		Type type = pt.getActualTypeArguments()[0];

		return type;
	}

	private String getTableName() {
		String actualType = getActualType().toString();
		return actualType.substring(actualType.lastIndexOf('.')+1);
		
	}

}
