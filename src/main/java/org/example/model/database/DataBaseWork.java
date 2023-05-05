package org.example.model.database;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.transaction.UserTransaction;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.ENameFiles;
import org.example.data.entity.EFile;
import org.example.data.entity.ELogin;

import java.util.ArrayList;
import java.util.List;

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
    public ELogin login(String login, String password) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return new ELogin("Error while Entity Manager initializing");
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from users where BINARY login = ? and BINARY password = ?", ELogin.class);

            query.setParameter(1, login)
                    .setParameter(2, password);

            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return new ELogin("Invalid username / mail or password");
            }

            ELogin eLogin = (ELogin) query.getSingleResult();
            Transaction.commit();

            return eLogin;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ELogin("Failed to connect to server.");
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
    public ArrayList<ENameFiles> allFiles(String userID, StringBuilder msg){
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                msg.append("Error while Entity Manager initializing");
                return null;
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where userid = ?", ENameFiles.class);
            query.setParameter(1, userID);

            if (query.getResultList().size() == 0) {
                Transaction.commit();

                msg.append("No file with that name was found.");
                return null;
            }

            List<ENameFiles> eFile = query.getResultList();

            Transaction.commit();
            return new ArrayList<>(eFile);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            msg.append("Error: " + e.getMessage());
            return null;
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public EFile loadFile(String fileName, String userID){
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return new EFile("Error while Entity Manager initializing");
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return new EFile("No file with that name was found.");
            }

            EFile eFile = (EFile) query.getSingleResult();

            Transaction.commit();
            return eFile;


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new EFile("Error: " + e.getMessage());
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
