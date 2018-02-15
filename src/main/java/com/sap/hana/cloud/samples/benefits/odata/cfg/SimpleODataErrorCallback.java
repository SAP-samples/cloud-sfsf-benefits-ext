package com.sap.hana.cloud.samples.benefits.odata.cfg;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.odata.AppODataException;

public class SimpleODataErrorCallback implements ODataErrorCallback {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleODataErrorCallback.class);

	@Override
	public ODataResponse handleError(final ODataErrorContext context) throws ODataApplicationException {
		Throwable rootCause = context.getException();
		LOGGER.error("Error in the OData. Reason: " + rootCause.getMessage(), rootCause); //$NON-NLS-1$

		Throwable innerCause = rootCause.getCause();
		if (rootCause instanceof ODataJPAException && innerCause != null && innerCause instanceof AppODataException) {
			context.setMessage(innerCause.getMessage());
			Throwable childInnerCause = innerCause.getCause();
			context.setInnerError(childInnerCause != null ? childInnerCause.getMessage() : ""); //$NON-NLS-1$
		} else {
			context.setMessage(HttpStatusCodes.INTERNAL_SERVER_ERROR.getInfo());
			context.setInnerError(rootCause.getMessage());
		}

		context.setHttpStatus(HttpStatusCodes.INTERNAL_SERVER_ERROR);
		return EntityProvider.writeErrorDocument(context);
	}
}
