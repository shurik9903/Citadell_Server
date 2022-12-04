package org.example.model.database;


public interface IDataBaseWork {
    Object login(String login, String Password);

    boolean ping();

}
