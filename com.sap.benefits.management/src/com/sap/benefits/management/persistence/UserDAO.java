package com.sap.benefits.management.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import com.sap.benefits.management.persistence.model.DBQueries;
import com.sap.benefits.management.persistence.model.User;

public class UserDAO extends BasicDAO<User>{
	
    public UserDAO() throws NamingException {
		super();
	}

	public List<User> getAllUsers() {
    	final EntityManager entityManager = factory.createEntityManager();
    	List<User> resut = new ArrayList<>();
    	try{
    		resut = entityManager.createNamedQuery(DBQueries.GET_ALL_USERS, User.class).getResultList();
    	}finally{
    		entityManager.close();
    	}
    	
        return resut;
    }
    
}

