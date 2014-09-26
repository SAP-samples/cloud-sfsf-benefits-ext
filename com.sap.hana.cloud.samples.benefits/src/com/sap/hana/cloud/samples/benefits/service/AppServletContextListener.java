package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.csv.dataimport.BenefitsDataImporter;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitDAO;
import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerFactoryProvider;
import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;

public class AppServletContextListener implements ServletContextListener {

	private static final String BENEFITS_CSV_PATH = "/benefits.csv"; //$NON-NLS-1$
	private static Logger logger = LoggerFactory.getLogger(AppServletContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		EntityManagerFactoryProvider.getInstance().close();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		try {
			EntityManagerProvider.getInstance().initEntityManagerProvider();
			initBenefits();
		} finally {
			EntityManagerProvider.getInstance().closeEntityManager();
		}
	}

	private void initBenefits() {
		final BenefitDAO benefitDAO = new BenefitDAO();
		if (benefitDAO.getAll().size() == 0) {
			final BenefitsDataImporter benefitImporter = new BenefitsDataImporter();
			try {
				benefitImporter.importDataFromCSV(BENEFITS_CSV_PATH);
			} catch (IOException e) {
				logger.error("Could not insert benefits data into DB", e); //$NON-NLS-1$
			}
		}

	}

}
