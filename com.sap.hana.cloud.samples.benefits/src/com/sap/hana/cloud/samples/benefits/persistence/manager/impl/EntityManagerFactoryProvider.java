package com.sap.hana.cloud.samples.benefits.persistence.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class EntityManagerFactoryProvider {
	
	private static EntityManagerFactoryProvider instance = null;
	
	private EntityManagerFactoryProvider(){
	}
	
	public static EntityManagerFactoryProvider getInstance(){
		if(instance == null){
			instance = new EntityManagerFactoryProvider();
		}
		
		return instance;
	}

	public EntityManagerFactory createEntityManagerFactory(DataSource dataSource) {
		final Map<Object, Object> properties = new HashMap<Object, Object>();
		properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
		return Persistence.createEntityManagerFactory("com.sap.hana.cloud.samples.benefits", properties);
	}

}
