package org.example.model.database;

import jakarta.persistence.EntityManager;

public interface IDBWork {
    void ping() throws Exception;

    EntityManager entityManagerConstructor() throws Exception;
}
