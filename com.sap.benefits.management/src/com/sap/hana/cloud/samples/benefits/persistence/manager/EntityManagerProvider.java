package com.sap.hana.cloud.samples.benefits.persistence.manager;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {

	EntityManager get();

}
