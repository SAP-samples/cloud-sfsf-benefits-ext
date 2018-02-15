package com.sap.hana.cloud.samples.benefits.odata.cfg;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.core.exception.ODataRuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerFactoryProvider;

public class BenefitsODataServiceFactory extends ODataJPAServiceFactory {
	private static final String PERSISTENCE_UNIT_NAME = "com.sap.hana.cloud.samples.benefits"; //$NON-NLS-1$

	private static final ThreadLocal<ODataContext> ODATA_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

	private static void setContextInThreadLocal(final ODataContext ctx) {
		ODATA_CONTEXT_THREAD_LOCAL.set(ctx);
	}

	public static void unsetContextInThreadLocal() {
		ODATA_CONTEXT_THREAD_LOCAL.remove();
	}

	public static ODataContext getContextInThreadLocal() {
		return ODATA_CONTEXT_THREAD_LOCAL.get();
	}

	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		ODataJPAContext oDataJPAContext = this.getODataJPAContext();
		try {
			oDataJPAContext.setEntityManagerFactory(EntityManagerFactoryProvider.getInstance().getEntityManagerFactory());
			oDataJPAContext.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
			oDataJPAContext.setJPAEdmExtension(new BenefitsJPAEdmExtension());

			setContextInThreadLocal(oDataJPAContext.getODataContext());
			return oDataJPAContext;
		} catch (Exception e) {
			throw new ODataRuntimeException("Cannot initialize OData JPA Context", e); //$NON-NLS-1$
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
