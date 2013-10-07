package com.sap.benefits.management.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test(@QueryParam("name") String name) {
		String response = "Hello " + name;
		return response;
	}
	
}