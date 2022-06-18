package com.vinni.webservices;

import java.util.Date;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {
    public static final boolean DEBUG = true;
    protected static EntityManagerFactory emf;
    private static final PersistenceManager singleton = new PersistenceManager();

    public static PersistenceManager getInstance() {
        return singleton;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            createEntityManagerFactory();
        }
        return emf;
    }

    public static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
            emf = null;
            System.out.println("n*** Persistence finished at " + new Date());
        }
    }

    private static void createEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("Gannon");
        System.out.println("n*** Persistence started at " + new Date());
    }
}
