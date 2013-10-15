package com.sap.benefits.management.services;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.benefits.management.csv.dataimport.BenefitsDataImporter;
import com.sap.benefits.management.persistence.BenefitDAO;

public class AppServletContextListener implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger(AppServletContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContext) {
        initBenefits();
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
