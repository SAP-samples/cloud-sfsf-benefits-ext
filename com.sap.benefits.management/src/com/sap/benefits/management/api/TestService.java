package com.sap.benefits.management.api;

import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.sap.benefits.management.persistance.Person;
import com.sap.benefits.management.persistance.PersonBean;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test() throws NamingException {
		Person marin = new Person();
		marin.setFirstName("Marin");
		marin.setLastName("Hadzhiev");
		
		
		PersonBean personBean = new PersonBean();
		personBean.addPerson(marin);
		
		List<Person> allPersons = personBean.getAllPersons();
		return new Gson().toJson(allPersons);
	}
	
}