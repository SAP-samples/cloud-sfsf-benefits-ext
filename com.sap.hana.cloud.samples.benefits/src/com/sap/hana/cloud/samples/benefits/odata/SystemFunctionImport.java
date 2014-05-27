package com.sap.hana.cloud.samples.benefits.odata;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.HttpMethod;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.commons.UIConfig;
import com.sap.hana.cloud.samples.benefits.commons.UserManager;
import com.sap.hana.cloud.samples.benefits.csv.dataimport.BenefitsDataImporter;
import com.sap.hana.cloud.samples.benefits.odata.cfg.ODataServiceFactory;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitDAO;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitTypeDAO;
import com.sap.hana.cloud.samples.benefits.persistence.OrderDAO;
import com.sap.hana.cloud.samples.benefits.persistence.OrderDetailDAO;
import com.sap.hana.cloud.samples.benefits.persistence.UserPointsDAO;
import com.sap.hana.cloud.samples.benefits.service.SessionListener;

public class SystemFunctionImport extends ODataService {
	private static final String BENEFITS_CSV_PATH = "/benefits.csv"; //$NON-NLS-1$
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EdmFunctionImport(name = "resetDB", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean resetDatabase() {
		cleanDB();
		forceSubsequentInitialization();

		final BenefitsDataImporter benefitImporter = new BenefitsDataImporter();
		try {
			benefitImporter.importDataFromCSV(BENEFITS_CSV_PATH);
		} catch (IOException e) {
			logger.error("Could not insert beneits data into DB", e); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	private void cleanDB() {
		final OrderDAO ordersDAO = new OrderDAO();
		final BenefitDAO benefitDAO = new BenefitDAO();
		final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
		final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
		final UserPointsDAO userPointsDAO = new UserPointsDAO();

		campaignDAO.deleteAll();
		userDAO.deleteAll();
		ordersDAO.deleteAll();
		benefitDAO.deleteAll();
		benefitTypeDAO.deleteAll();
		orderDetailDAO.deleteAll();
		userPointsDAO.deleteAll();
	}

	private void forceSubsequentInitialization() {
		ODataContext ctx = ODataServiceFactory.getContextInThreadLocal();
		HttpServletRequest httpServlReq = (HttpServletRequest) ctx.getParameter(ODataContext.HTTP_SERVLET_REQUEST_OBJECT);
		httpServlReq.getSession().setAttribute(SessionListener.INITIAL_FLAG, "true");
	}

	@EdmFunctionImport(name = "uiConfig", returnType = @ReturnType(type = Type.COMPLEX, isCollection = false), httpMethod = HttpMethod.GET)
	public UIConfig getUIConfigurationData() {
		final UIConfig config = new UIConfig();
		if (UserManager.getIsUserAdmin()) {
			config.initAdminConfiguration();
		} else {
			config.initEmployeeConfiguration();
		}

		return config;
	}
}
