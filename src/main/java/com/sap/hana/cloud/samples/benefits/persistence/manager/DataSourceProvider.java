package com.sap.hana.cloud.samples.benefits.persistence.manager;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static DataSource dataSource = null;
	private static DataSourceProvider instance;

	public synchronized static DataSourceProvider getInstance() {
		if (instance == null) {
			instance = new DataSourceProvider();
		}
		return instance;
	}

	private DataSourceProvider() {
		try {
			final InitialContext ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB"); //$NON-NLS-1$
		} catch (NamingException e) {
			logger.error("Could not lookup the default datasource", e); //$NON-NLS-1$
			throw new RuntimeException(e);
		}
	}

	public DataSource get() {
		return dataSource;
	}

}
