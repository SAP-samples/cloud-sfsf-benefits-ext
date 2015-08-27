<%@page
	import="com.sap.hana.cloud.samples.benefits.connectivity.http.SimpleHttpResponse"%><%@page
	import="com.sap.hana.cloud.samples.benefits.connectivity.http.HTTPConnector"%><%@page contentType="text/xml;;charset=UTF-8"%><%
    try {
        HTTPConnector httpConnector = new HTTPConnector("sap_jam_odata");
        SimpleHttpResponse httpResponse = httpConnector.executePOST("/v1/single_use_tokens", null, null);

        if (httpResponse.getContent() != null) {
            String singleUseTokenXML = httpResponse.getContent();
            //String outputString = singleUseTokenXML.substring(61,97);
            //out.print(outputString);
            out.write(singleUseTokenXML);
            //out.println(singleUseTokenXML);		
        } else
            out.println("There was a problem with the connection");
    } catch (Exception ex) {
        System.err.println(ex.toString());
        out.println(ex.toString());
    }
%>