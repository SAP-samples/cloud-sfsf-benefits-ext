package com.sap.hana.cloud.samples.benefits.persistence.manager;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class EntityManagerFactoryProvider {

	private static EntityManagerFactoryProvider instance = null;

	private EntityManagerFactory factory = null;

	private EntityManagerFactoryProvider() {
	}

	public static synchronized EntityManagerFactoryProvider getInstance() {
		if (instance == null) {
			instance = new EntityManagerFactoryProvider();
		}

		return instance;
	}

	public synchronized EntityManagerFactory getEntityManagerFactory() {
		if (this.factory == null) {
			this.factory = createEntityManagerFactory(DataSourceProvider.getInstance().get());
		}
		return this.factory;
	}

	private EntityManagerFactory createEntityManagerFactory(DataSource dataSource) {
		final Map<Object, Object> properties = new HashMap<>();
		properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
		return Persistence.createEntityManagerFactory("com.sap.hana.cloud.samples.benefits", properties); //$NON-NLS-1$
	}

	public synchronized void close() {
		if (this.factory != null) {
			this.factory.close();
			this.factory = null;
		}
	}

}
