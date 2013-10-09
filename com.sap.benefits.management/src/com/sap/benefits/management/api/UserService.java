package com.sap.benefits.management.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserService {

	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public SFUser getUserProfile() {
		return new SFUser();
	}

}
