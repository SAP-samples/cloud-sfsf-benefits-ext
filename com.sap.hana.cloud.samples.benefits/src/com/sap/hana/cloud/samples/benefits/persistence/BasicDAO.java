package com.sap.hana.cloud.samples.benefits.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;
import com.sap.hana.cloud.samples.benefits.persistence.model.IDBEntity;

public class BasicDAO<T extends IDBEntity> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected EntityManagerProvider emProvider;

	public BasicDAO(EntityManagerProvider emProvider) {
		this.emProvider = emProvider;
	}

	public void refresh(T object) {
		final EntityManager em = emProvider.get();
		em.refresh(object);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		final EntityManager em = emProvider.get();
		TypedQuery<? extends Type> query = em.createQuery("select t from " + getTableName() + " t", //$NON-NLS-1$ //$NON-NLS-2$
				this.getClass().getGenericSuperclass().getClass());
		return (List<T>) query.getResultList();
	}

	public T save(T entity) {
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();

		final T merge = em.merge(entity);

		em.getTransaction().commit();
		return merge;
	}

	public T saveNew(T entity) {
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();
		em.persist(entity);

		em.getTransaction().commit();
		return entity;
	}

	public void deleteAll() {
		final List<T> all = getAll();
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();

		for (T t : all) {
			final T managedObject = getById(t.getId(), em);
			em.remove(managedObject);
		}

		em.getTransaction().commit();
	}

	public T getById(long id) {
		final EntityManager em = emProvider.get();
		return getById(id, em);
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
		} catch (NonUniqueResultException e) {
			logger.error("More than one entity {} from table {}.", id, getTableName()); //$NON-NLS-1$
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
