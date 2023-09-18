package org.example.model.database.fileWork;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.EFile;
import org.example.data.entity.ENameFiles;
import org.example.model.database.DBConstructor;

import java.util.ArrayList;
import java.util.List;

public class DBFileWork extends DBConstructor implements IDBFileWork {

    @Override
    public void saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and user_id = ?::integer", EFile.class);
            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() != 0) {
                transaction.commit();
                replace.setTrue();
                return;
            }

            query = entityManager.createNativeQuery("Insert into user_tables (name, file, user_id) values (?, ?, ?::integer)");
            query.setParameter(1, fileName)
                    .setParameter(2, fileByte)
                    .setParameter(3, userID)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public void overwriteFile(String fileName, byte[] fileByte, String userID) throws Exception {


        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and user_id = ?::integer", EFile.class);
            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Файл с таким именем не найден");
            }

            EFile eFile = (EFile) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Update user_tables set name = ?, file = ?, user_id = ?::integer where id = ?::integer");
            query.setParameter(1, fileName)
                    .setParameter(2, fileByte)
                    .setParameter(3, userID)
                    .setParameter(4, eFile.getFile_id())
                    .executeUpdate();

            transaction.commit();


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public ArrayList<ENameFiles> allFiles(String userID) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where user_id = ?::integer", ENameFiles.class);
            query.setParameter(1, userID);


            List<ENameFiles> eFile = query.getResultList();

            transaction.commit();
            return new ArrayList<>(eFile);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public EFile loadFile(String fileName, String userID) throws Exception {

        System.out.println("loadfile");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and user_id = ?::integer", EFile.class);
            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw  new Exception("Файл с таким именем не найден");
            }

            EFile eFile = (EFile) query.getResultList().get(query.getFirstResult());

            transaction.commit();
            return eFile;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public void deleteFile(String fileName, String userID) throws Exception {

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();


            Query query = entityManager.createNativeQuery("Select * from user_tables where name = ? and user_id = ?::integer", ENameFiles.class);
            query.setParameter(1, fileName)
                    .setParameter(2, userID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Файл с таким именем не найден");
            }

            ENameFiles eFile = (ENameFiles) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Delete from user_reports where user_id = ?::integer and table_id = ?::integer");
            query.setParameter(1, userID)
                    .setParameter(2, eFile.getFile_id()).executeUpdate();

//            query.executeUpdate();

            query = entityManager.createNativeQuery("Delete from user_tables where name = ? and user_id = ?::integer");
//            query.executeUpdate();
            query.setParameter(1, fileName)
                    .setParameter(2, userID).executeUpdate();

//            query.executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }




}
