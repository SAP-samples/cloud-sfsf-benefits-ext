package com.sap.hana.cloud.samples.benefits.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.persistence.model.IDBEntity;
import com.sap.hana.cloud.samples.benefits.persistence.util.DataSourceProvider;
import com.sap.hana.cloud.samples.benefits.persistence.util.EntityManagerFactoryProvider;

public class BasicDAO<T extends IDBEntity> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected EntityManagerFactory factory;

	public BasicDAO() {
		try {
			DataSource dataSource = DataSourceProvider.getInstance().getDefault();
			this.factory = EntityManagerFactoryProvider.getInstance().createEntityManagerFactory(dataSource);
		} catch (NamingException e) {
			logger.error("Could not get default data source", e);
			throw new RuntimeException();
		}
	}
	
	public void refresh(T object){
		final EntityManager em = factory.createEntityManager();
		try {
			em.refresh(object);
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		final List<T> result = new ArrayList<>();
		final EntityManager em = factory.createEntityManager();
		try {
			result.addAll((Collection<? extends T>) em.createQuery("select t from " + getTableName() + " t",
					this.getClass().getGenericSuperclass().getClass()).getResultList());
		} finally {
			em.close();
		}

		return result;
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
		final List<T> all = getAll();
		final EntityManager em = factory.createEntityManager();

		try {
			em.getTransaction().begin();

			for (T t : all) {
				final T managedObject = getById(t.getId(), em);
				em.remove(managedObject);
			}

			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public T getById(long id) {
		final EntityManager em = factory.createEntityManager();
		try {
			return getById(id, em);
		} finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	private T getById(long id, EntityManager em) {
		T t = null;

		try {
			Query query = em.createQuery("select u from " + getTableName() + " u where u.id = :id"); //$NON-NLS-1$ //$NON-NLS-2$
			query.setParameter("id", id); //$NON-NLS-1$
			t = (T) query.getSingleResult();
		} catch (NoResultException e) {
			logger.error("Could not retrieve entity {} from table {}.", id, getTableName()); //$NON-NLS-1$
		}

		return t;
	}

	private Type getActualType() {
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) genericSuperclass;
		Type type = pt.getActualTypeArguments()[0];

		return type;
	}

	private String getTableName() {
		String actualType = getActualType().toString();
		return actualType.substring(actualType.lastIndexOf('.') + 1);

	}

}
