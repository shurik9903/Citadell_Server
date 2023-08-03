package org.example.model.database.reportWork;

import org.example.data.entity.EReport;
import org.example.data.mydata.DReport;
import org.example.model.database.IDBWork;

import java.util.ArrayList;

public interface IDBReportWork extends IDBWork {

    void saveReports(String fileName, String userID, ArrayList<DReport> reports) throws Exception;

    ArrayList<EReport> loadReports(String fileName, String userID) throws Exception;

}
