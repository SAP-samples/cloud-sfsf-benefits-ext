package com.sap.benefits.management.persistance;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * Class holding information on a person.
 */
@Entity
@Table(name = "T_PERSON")
@NamedQuery(name = "AllPersons", query = "select p from Person p")
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
