package com.gannon.webservices;

import java.util.Date;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {
	protected EntityManagerFactory emf;
	private static final PersistenceManager singleton = new PersistenceManager();

	public static PersistenceManager getInstance() {
		return singleton;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		if (emf == null) {
			createEntityManagerFactory();
		}
		return emf;
	}

	public void closeEntityManagerFactory() {
		if (emf != null) {
			emf.close();
			emf = null;
			System.out.println("n*** Persistence finished at " + new Date());
		}
	}

	private void createEntityManagerFactory() {
		emf = Persistence.createEntityManagerFactory("Gannon");
		System.out.println("n*** Persistence started at " + new Date());
	}
}
