package com.sap.hana.cloud.samples.benefits.odata.cfg;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.core.exception.ODataRuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.odata.ProcessingExtension;
import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerFactoryProvider;

public class ODataServiceFactory extends ODataJPAServiceFactory {
	private static final String PERSISTENCE_UNIT_NAME = "com.sap.hana.cloud.samples.benefits";

	private static final Logger LOGGER = LoggerFactory.getLogger(ODataServiceFactory.class);

	private static final ThreadLocal<ODataContext> oDataContextThreadLocal = new ThreadLocal<ODataContext>();

	private static void setContextInThreadLocal(final ODataContext ctx) {
		oDataContextThreadLocal.set(ctx);
	}

	public static void unsetContextInThreadLocal() {
		oDataContextThreadLocal.remove();
	}

	public static ODataContext getContextInThreadLocal() {
		return oDataContextThreadLocal.get();
	}

	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		ODataJPAContext oDataJPAContext = this.getODataJPAContext();
		try {
			oDataJPAContext.setEntityManagerFactory(EntityManagerFactoryProvider.getInstance().getEntityManagerFactory());
			oDataJPAContext.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
			oDataJPAContext.setJPAEdmExtension(new ProcessingExtension());

			setContextInThreadLocal(oDataJPAContext.getODataContext());
			return oDataJPAContext;
		} catch (Exception e) {
			throw new ODataRuntimeException("Cannot initialize OData JPA Context", e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ODataCallback> T getCallback(final Class<? extends ODataCallback> callbackInterface) {
		setDetailErrors(true);

		if (callbackInterface.isAssignableFrom(ODataErrorCallback.class)) {
			return (T) new SimpleODataErrorCallback();
		}
		return null;
	}

}
