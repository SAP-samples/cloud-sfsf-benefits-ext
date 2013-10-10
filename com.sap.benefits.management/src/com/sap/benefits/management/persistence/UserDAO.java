package com.sap.benefits.management.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sap.benefits.management.persistence.model.DBQueries;
import com.sap.benefits.management.persistence.model.User;

public class UserDAO extends BasicDAO<User>{
	
    public UserDAO() {
		super();
	}
    
    public User getByUserId(String userId){
    	final EntityManager em = factory.createEntityManager();
		try {
			em.getTransaction().begin();
			
			final Query query = em.createNamedQuery(DBQueries.GET_USER_BY_USER_ID, User.class);
			query.setParameter("userId", userId);
			final User user = (User) query.getSingleResult();
			em.getTransaction().commit();
			
			return user;
		} finally {
			em.close();
		}
    }

}

