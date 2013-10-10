package com.sap.benefits.management.api;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.benefits.management.connectivity.CoreODataConnector;
import com.sap.benefits.management.connectivity.helper.SFUser;

@Path("/user")
public class UserService {

	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public SFUser getUserProfile() throws IOException {
		return CoreODataConnector.getInstance().getUserProfile("nnnn");
	}
	
	@GET
	@Path("/managed")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SFUser> getManagedUsers() throws IOException {
		return CoreODataConnector.getInstance().getManagedEmployees("nnnn");
	}

}
