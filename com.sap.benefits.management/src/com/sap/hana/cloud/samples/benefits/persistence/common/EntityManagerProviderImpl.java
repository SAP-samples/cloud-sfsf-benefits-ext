package com.sap.hana.cloud.samples.benefits.persistence.common;

import javax.persistence.EntityManager;

import com.sap.hana.cloud.samples.benefits.persistence.EntityManagerProvider;

public class EntityManagerProviderImpl  implements EntityManagerProvider
{  
    public static final ThreadLocal<EntityManager> ENTITY_MANAGER = new ThreadLocal<EntityManager>();  
  
	@Override
	public EntityManager get() {
		return ENTITY_MANAGER.get(); 
	}
	
	public void set(EntityManager entityManager){
		ENTITY_MANAGER.set(entityManager);
	}
	
	public void remove(){
		ENTITY_MANAGER.remove();
	}
}  
