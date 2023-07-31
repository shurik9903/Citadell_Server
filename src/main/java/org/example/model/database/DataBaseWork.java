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
import org.example.data.entity.EReport;

import org.example.data.mydata.DReport;

import java.util.ArrayList;
import java.util.List;

public class DataBaseWork implements IDataBaseWork {

    //@PersistenceUnit(unitName = "CitadelJDBC_PersistenceUnit")
    private EntityManagerFactory EMF;

//    @PostConstruct
//    public void PersisInit(){
//        EMF = Persistence.createEntityManagerFactory("CitadelJDBC_PersistenceUnit");
//    }

    @PostConstruct
    public void PersisInit(){
        EMF = Persistence.createEntityManagerFactory("CitadelJDBCPG_PersistenceUnit");
    }



    @Resource
    private UserTransaction transaction;

    @Override
    public ELogin login(String login, String password) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return new ELogin("Ошибка при инициализации Entity Manager");
            }

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from users where BINARY login = ? and BINARY password = ?", ELogin.class);

            Query query = entityManager.createNativeQuery("Select * from users where login = ? and password = ?", ELogin.class);


            query.setParameter(1, login)
                    .setParameter(2, password);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return new ELogin("Неверный логин или пароль");
            }

            ELogin eLogin = (ELogin) query.getSingleResult();
            transaction.commit();

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

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);
            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?::integer", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);


            if (query.getResultList().size() == 0) {
                transaction.commit();
                return "Файл с таким именем не найден";
            }

            EFile eFile = (EFile) query.getSingleResult();

            for (DReport report : reports) {

//                query = entityManager.createNativeQuery("Select * from user_reports where user_id = ? and table_id = ? and row_num = ?", EReport.class);
                query = entityManager.createNativeQuery("Select * from user_reports where user_id = ?::integer and table_id = ?::integer and row_num = ?::integer", EReport.class);
                query.setParameter(1, userID)
                        .setParameter(2, eFile.getFile_id())
                        .setParameter(3, report.getRowNum());


                if (query.getResultList().size() == 0) {
//                    query = entityManager.createNativeQuery("Insert into user_reports (table_id, user_id, message, row_num) values (?, ?, ?, ?)");
                    query = entityManager.createNativeQuery("Insert into user_reports (table_id, user_id, message, row_num) values (?::integer, ?::integer, ?, ?::integer)");
                    query.setParameter(1, eFile.getFile_id())
                            .setParameter(2, userID)
                            .setParameter(3, report.getMessage())
                            .setParameter(4, report.getRowNum())
                            .executeUpdate();
                } else {
                    EReport eReport = (EReport) query.getSingleResult();
//                    query = entityManager.createNativeQuery("Update user_reports set message = ? where id = ?");
                    query = entityManager.createNativeQuery("Update user_reports set message = ? where id = ?::integer");
                    query.setParameter(1, report.getMessage())
                            .setParameter(2, eReport.getReportID())
                            .executeUpdate();
                }
            }

            transaction.commit();
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
    public ArrayList<EReport> loadReports(String fileName, String userID, StringBuilder msg) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                msg.append("Ошибка при инициализации Entity Manager");
            }

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);
            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?::integer", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);


            if (query.getResultList().size() == 0) {
                transaction.commit();
                msg.append("Файл с таким именем не найден");
            }

            EFile eFile = (EFile) query.getSingleResult();

//                query = entityManager.createNativeQuery("Select * from user_reports where user_id = ? and table_id = ?", EReport.class);
            query = entityManager.createNativeQuery("Select * from user_reports where user_id = ?::integer and table_id = ?::integer", EReport.class);
                query.setParameter(1, userID)
                        .setParameter(2, eFile.getFile_id());


            List<EReport> eReports = query.getResultList();


            transaction.commit();
            return new ArrayList<>(eReports);

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
    public String saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace) {
        EntityManager entityManager = null;
        try {
            try {
                entityManager = EMF.createEntityManager();
            } catch (Exception e) {
                return "Ошибка при инициализации Entity Manager";
            }

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);
            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?::integer", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() != 0) {
                transaction.commit();
                replace.setTrue();
                return "";
            }

//            query = entityManager.createNativeQuery("Insert into user_tables (name, file, userid) values (?, ?, ?)");
            query = entityManager.createNativeQuery("Insert into user_tables (name, file, userid) values (?, ?, ?::integer)");
            query.setParameter(1, fileName)
                    .setParameter(2, fileByte)
                    .setParameter(3, userID)
                    .executeUpdate();

            transaction.commit();
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

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);
            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?::integer", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return "Файл с таким именем не найден";
            }

            EFile eFile = (EFile) query.getSingleResult();

//            query = entityManager.createNativeQuery("Update user_tables set name = ?, file = ?, userid = ? where id = ?");
            query = entityManager.createNativeQuery("Update user_tables set name = ?, file = ?, userid = ?::integer where id = ?::integer");
            query.setParameter(1, fileName)
                    .setParameter(2, fileByte)
                    .setParameter(3, userID)
                    .setParameter(4, eFile.getFile_id())
                    .executeUpdate();

            transaction.commit();
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

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from user_tables where userid = ?", ENameFiles.class);
            Query query = entityManager.createNativeQuery("Select * from user_tables where userid = ?::integer", ENameFiles.class);
            query.setParameter(1, userID);

//            if (query.getResultList().size() == 0) {
//                transaction.commit();
//
//                msg.append("Файл с таким именем не найден");
//                return null;
//            }

            List<ENameFiles> eFile = query.getResultList();

            transaction.commit();
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

            transaction.begin();
            entityManager.joinTransaction();

//            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?", EFile.class);
            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and userid = ?::integer", EFile.class);

            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return new EFile("Файл с таким именем не найден");
            }

            EFile eFile = (EFile) query.getSingleResult();

            transaction.commit();
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
