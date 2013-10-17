package com.sap.hana.cloud.samples.benefits.persistence;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {

	EntityManager get();

}
