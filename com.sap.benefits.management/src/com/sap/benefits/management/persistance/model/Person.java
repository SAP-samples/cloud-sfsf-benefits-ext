package com.sap.benefits.management.persistance.model;

import static com.sap.benefits.management.persistance.DBQueries.DELETE_ALL_PERSONS;
import static com.sap.benefits.management.persistance.DBQueries.GET_ALL_PERSONS;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Class holding information on a person.
 */
@Entity
@Table(name = "T_PERSON")
@NamedQueries({ 
	@NamedQuery(name = GET_ALL_PERSONS, query = "select p from Person p"),
	@NamedQuery(name = DELETE_ALL_PERSONS, query = "delete from Person p")
})
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    @Basic
    private String firstName;
    @Basic
    private String lastName;

    public long getId() {
        return id;
    }

    public void setId(long newId) {
        this.id = newId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }
}
