package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.csv.dataimport.BenefitsDataImporter;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitDAO;
import com.sap.hana.cloud.samples.benefits.persistence.manager.PersistenceManager;

public class AppServletContextListener implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(AppServletContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		try {
			PersistenceManager.getInstance().initEntityManagerProvider();
			initBenefits();
		} finally {
			PersistenceManager.getInstance().closeAll();
		}
	}

	private void initBenefits() {
		final BenefitDAO benefitDAO = new BenefitDAO();
		if (benefitDAO.getAll().size() == 0) {
			final BenefitsDataImporter benefitImporter = new BenefitsDataImporter();
			try {
				benefitImporter.importData("/benefits.csv");
			} catch (IOException e) {
				logger.error("Could not insert beneits data into DB", e);
			}
		}

	}

}
