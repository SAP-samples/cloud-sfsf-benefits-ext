package com.sap.hana.cloud.samples.benefits.odata.cfg;

import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleODataErrorCallback implements ODataErrorCallback {

	final String SEPARATOR = " : "; //$NON-NLS-1$

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleODataErrorCallback.class);

	@Override
	public ODataResponse handleError(final ODataErrorContext context) throws ODataApplicationException {

		Throwable t = context.getException();
		LOGGER.error("Error in the OData.", t); //$NON-NLS-1$
		if (t instanceof ODataJPAException) {
			StringBuilder errorBuilder = new StringBuilder();
			errorBuilder.append(t.getCause().getClass().toString());
			errorBuilder.append(SEPARATOR);
			errorBuilder.append(t.getCause().getMessage());
			context.setInnerError(errorBuilder.toString());
		}
		return EntityProvider.writeErrorDocument(context);
	}

}
