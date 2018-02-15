package com.sap.hana.cloud.samples.benefits.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

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

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		final List<T> result = new ArrayList<>();
		final EntityManager em = emProvider.get();
		result.addAll((Collection<? extends T>) em.createQuery("select t from " + getTableName() + " t", //$NON-NLS-1$ //$NON-NLS-2$
				this.getClass().getGenericSuperclass().getClass()).getResultList());
		return result;
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
			throw new IllegalStateException(String.format("More than one entity %s from table %s.", id, getTableName())); //$NON-NLS-1$
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
