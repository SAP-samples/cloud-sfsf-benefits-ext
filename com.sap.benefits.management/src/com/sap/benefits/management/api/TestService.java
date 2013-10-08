package com.sap.benefits.management.api;

import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.sap.benefits.management.persistance.PersonDAO;
import com.sap.benefits.management.persistance.model.Person;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test() throws NamingException {
		final PersonDAO personDAO = new PersonDAO();
		personDAO.deleteAll();
		
		final Person marin = new Person();
		marin.setFirstName("Marin");
		marin.setLastName("Hadzhiev");
		
		
		personDAO.addPerson(marin);
		
		List<Person> allPersons = personDAO.getAllPersons();
		return new Gson().toJson(allPersons);
	}
	
}