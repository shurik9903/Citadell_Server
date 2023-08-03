package org.example.model.database.reportWork;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.example.data.entity.ENameFiles;
import org.example.data.entity.EFile;
import org.example.data.entity.EReport;

import org.example.data.mydata.DReport;
import org.example.model.database.DBConstructor;

import java.util.ArrayList;
import java.util.List;

public class DBReportWork extends DBConstructor implements IDBReportWork {

    @Override
    public void saveReports(String fileName, String userID, ArrayList<DReport> reports) throws Exception {
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

            for (DReport report : reports) {

                query = entityManager.createNativeQuery("Select * from user_reports where user_id = ?::integer and table_id = ?::integer and row_num = ?::integer", EReport.class);
                query.setParameter(1, userID)
                        .setParameter(2, eFile.getFile_id())
                        .setParameter(3, report.getRowNum());


                if (query.getResultList().size() == 0) {
                    query = entityManager.createNativeQuery("Insert into user_reports (table_id, user_id, message, row_num) values (?::integer, ?::integer, ?, ?::integer)");
                    query.setParameter(1, eFile.getFile_id())
                            .setParameter(2, userID)
                            .setParameter(3, report.getMessage())
                            .setParameter(4, report.getRowNum())
                            .executeUpdate();
                } else {
                    EReport eReport = (EReport) query.getResultList().get(query.getFirstResult());
                    query = entityManager.createNativeQuery("Update user_reports set message = ? where id = ?::integer");
                    query.setParameter(1, report.getMessage())
                            .setParameter(2, eReport.getReportID())
                            .executeUpdate();
                }
            }

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
    public ArrayList<EReport> loadReports(String fileName, String userID) throws Exception {
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

            query = entityManager.createNativeQuery("Select * from user_reports where user_id = ?::integer and table_id = ?::integer", EReport.class);
            query.setParameter(1, userID)
                    .setParameter(2, eFile.getFile_id());

            List<EReport> eReports = query.getResultList();

            transaction.commit();
            return new ArrayList<>(eReports);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }


}
