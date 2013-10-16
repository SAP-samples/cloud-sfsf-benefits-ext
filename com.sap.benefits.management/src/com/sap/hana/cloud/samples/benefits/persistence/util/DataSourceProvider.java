package com.sap.hana.cloud.samples.benefits.persistence.util;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceProvider {

	private static final String DEFAULT_DATA_SOURCE = "DEFAULT_DS";

	private static Map<String, DataSource> servicesDataSources = new HashMap<String, DataSource>();
	private static DataSourceProvider instance;

	public static DataSourceProvider getInstance() {
		if (instance == null) {
			instance = new DataSourceProvider();
		}

		return instance;
	}

	public DataSource getDefault() throws NamingException {
		if (servicesDataSources.get(DEFAULT_DATA_SOURCE) == null) {
			final InitialContext ctx = new InitialContext();
			DataSource defaultDataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			servicesDataSources.put(DEFAULT_DATA_SOURCE, defaultDataSource);
		}

		return servicesDataSources.get(DEFAULT_DATA_SOURCE);
	}

}
