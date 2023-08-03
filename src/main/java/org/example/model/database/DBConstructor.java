package org.example.model.database;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.UserTransaction;

public class DBConstructor implements IDBWork {

    //@PersistenceUnit(unitName = "CitadellJDBC_PersistenceUnit")
    protected EntityManagerFactory EMF;

//    @PostConstruct
//    public void PersisInit(){
//        EMF = Persistence.createEntityManagerFactory("CitadellJDBC_PersistenceUnit");
//    }

    @PostConstruct
    public void PersisInit(){
        EMF = Persistence.createEntityManagerFactory("CitadellJDBCPG_PersistenceUnit");
    }

    @Resource
    protected UserTransaction transaction;

    @Override
    public void ping() throws Exception {
        try {
            EMF.createEntityManager();
        }catch (Exception e){
            throw new Exception("Нет соединения с базой данных");
        }
    }

    @Override
    public EntityManager entityManagerConstructor() throws Exception {
        try {
            return EMF.createEntityManager();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка при инициализации Entity Manager");
        }
    }

}
