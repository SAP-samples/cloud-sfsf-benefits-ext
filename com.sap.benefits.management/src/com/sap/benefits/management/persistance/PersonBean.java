package com.sap.benefits.management.persistance;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import com.sap.benefits.management.persistance.util.DataSourceProvider;
import com.sap.benefits.management.persistance.util.EntityManagerFactoryProvider;

public class PersonBean {
	
    private EntityManagerFactory factory;
    
    public PersonBean() throws NamingException {
    	this.factory = EntityManagerFactoryProvider.getInstance().createEntityManagerFactory(DataSourceProvider.getInstance().getDefault());
	}
    
    public List<Person> getAllPersons() {
    	final EntityManager entityManager = factory.createEntityManager();
    	List<Person> resut = new ArrayList<>();
    	try{
    		resut = entityManager.createNamedQuery("AllPersons", Person.class).getResultList();
    	}finally{
    		entityManager.close();
    	}
    	
        return resut;
    }

    public void addPerson(Person person) {
    	final EntityManager entityManager = factory.createEntityManager();
    	try{
    		final EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			
    		entityManager.persist(person);
    		entityManager.flush();
    		
    		transaction.commit();
    	}finally{
    		entityManager.close();
    	}
    }
}

