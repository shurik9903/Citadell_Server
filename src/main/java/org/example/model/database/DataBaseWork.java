package org.example.model.database;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.transaction.UserTransaction;

import org.example.data.entity.ELogin;
import org.example.data.mydata.DLogin;

import java.util.Objects;


public class DataBaseWork implements IDataBaseWork {

    //@PersistenceUnit(unitName = "FSBJDBC_PersistenceUnit")
    private EntityManagerFactory EMF;

    @PostConstruct
    public void PersisInit(){
        EMF = Persistence.createEntityManagerFactory("FSBJDBC_PersistenceUnit");
    }

    @Resource
    private UserTransaction Transaction;

    @Override
    public Object login(String login, String password) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return new DLogin("Error while Entity Manager initializing", -1, null);
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from users where BINARY login = ? and BINARY password = ?", ELogin.class);

            query.setParameter(1, login)
                    .setParameter(2, password);


            DLogin dlogin = null;

            try {
                dlogin  = new DLogin((ELogin) query.getSingleResult());
            } catch (Exception ignored) {}

            Transaction.commit();

            return Objects.requireNonNullElseGet(dlogin, () -> new DLogin("Invalid username / mail or password", -1, null));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new DLogin("Failed to connect to server", -1, null);
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public boolean ping(){
        try {
            EMF.createEntityManager();
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
