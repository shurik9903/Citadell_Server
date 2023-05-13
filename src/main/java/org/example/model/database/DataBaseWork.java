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
import org.example.data.mydata.DChangeData;
import org.example.data.mydata.DReport;

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
                return new ELogin("Ошибка при инициализации Entity Manager");
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from users where BINARY login = ? and BINARY password = ?", ELogin.class);

            query.setParameter(1, login)
                    .setParameter(2, password);

            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return new ELogin("Неверный логин или пароль");
            }

            ELogin eLogin = (ELogin) query.getSingleResult();
            Transaction.commit();

            return eLogin;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ELogin("Нет соединения с базой данных");
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public String saveReports(String fileName, String userID, ArrayList<DReport> reports) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return "Ошибка при инициализации Entity Manager";
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);


            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return "Файл с таким именем не найден";
            }

            EFile eFile = (EFile) query.getSingleResult();

            for (DReport report : reports) {
                query = entityManager.createNativeQuery("Insert into user_reports (table_id, user_id, message, row) values (?, ?, ?, ?)");
                query.setParameter(1, eFile.getFile_id())
                        .setParameter(2, userID)
                        .setParameter(3, report.getMessage())
                        .setParameter(4, report.getRowNum())
                        .executeUpdate();
            }

            Transaction.commit();
            return "";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Ошибка: " + e.getMessage();
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
                return "Ошибка при инициализации Entity Manager";
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() != 0) {
                Transaction.commit();
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
            return "Ошибка: " + e.getMessage();
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
                return "Ошибка при инициализации Entity Manager";
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return "Файл с таким именем не найден";
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
            return "Ошибка: " + e.getMessage();
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
                msg.append("Ошибка при инициализации Entity Manager");
                return null;
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where userid = ?", ENameFiles.class);
            query.setParameter(1, userID);

            if (query.getResultList().size() == 0) {
                Transaction.commit();

                msg.append("Файл с таким именем не найден");
                return null;
            }

            List<ENameFiles> eFile = query.getResultList();

            Transaction.commit();
            return new ArrayList<>(eFile);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            msg.append("Ошибка: " + e.getMessage());
            return null;
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public EFile loadFile(String fileName, String userID){

        System.out.println("test " + fileName);


        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return new EFile("Ошибка при инициализации Entity Manager");
            }

            Transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                Transaction.commit();
                return new EFile("Файл с таким именем не найден");
            }

            EFile eFile = (EFile) query.getSingleResult();

            Transaction.commit();
            return eFile;


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new EFile("Ошибка " + e.getMessage());
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
