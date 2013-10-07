package com.sap.benefits.management.persistance;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Bean encapsulating all operations for a person.
 */
@Stateless
@LocalBean
public class PersonBean {
	
    @PersistenceContext
    private EntityManager em;

    /**
     * Get all persons from the table.
     */
    public List<Person> getAllPersons() {
        return em.createNamedQuery("AllPersons", Person.class).getResultList();
    }

    /**
     * Add a person to the table.
     */
    public void addPerson(Person person) {
        em.persist(person);
        em.flush();
    }
}
