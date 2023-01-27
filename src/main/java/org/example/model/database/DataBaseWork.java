package org.example.model.database;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.transaction.UserTransaction;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.EFile;
import org.example.data.entity.ELogin;
import org.example.data.mydata.DFile;
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
            return new DLogin("Failed to connect to server.", -1, null);
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public String saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return "Error while Entity Manager initializing";
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() != 0) {
                Transaction.commit();
//                replace.append("A file with the same name already exists.");
                replace.setTrue();
                return "";
            }

            query = entityManager.createNativeQuery("Insert into user_tables (name, file, userid) values (?, ?, ?)");
            query.setParameter(1, fileName)
                    .setParameter(2, fileByte)
                    .setParameter(3, userID)
                    .executeUpdate();

            Transaction.commit();
            return "";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public String overwriteFile(String fileName, byte[] fileByte, String userID){
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return "Error while Entity Manager initializing";
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return "No file with that name was found.";
            }

            EFile eFile = (EFile) query.getSingleResult();

            query = entityManager.createNativeQuery("Update user_tables set name = ?, file = ?, userid = ? where id = ?");
            query.setParameter(1, fileName)
                    .setParameter(2, fileByte)
                    .setParameter(3, userID)
                    .setParameter(4, eFile.getFile_id())
                    .executeUpdate();

            Transaction.commit();
            return "";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public DFile loadFile(String fileName, String userID){
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return new DFile("Error while Entity Manager initializing");
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            DFile dFile = null;

            try {
                dFile  = new DFile((EFile) query.getSingleResult());
            } catch (Exception ignored) {}

            Transaction.commit();
            return Objects.requireNonNullElseGet(dFile, () -> new DFile("Invalid username / mail or password"));


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new DFile("Error: " + e.getMessage());
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
