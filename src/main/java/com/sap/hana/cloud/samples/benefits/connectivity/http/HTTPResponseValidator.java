package com.sap.hana.cloud.samples.benefits.connectivity.http;

public interface HTTPResponseValidator {
	
   void validateHTTPResponse(SimpleHttpResponse httpResponse) throws InvalidResponseException;

}
