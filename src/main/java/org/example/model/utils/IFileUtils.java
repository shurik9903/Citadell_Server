package org.example.model.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.example.data.mydata.DReport;
import org.example.data.mydata.DUserConnect;

import java.io.IOException;
import java.util.ArrayList;

public interface IFileUtils {
    boolean writeFile(byte[] content, String filename) throws IOException;

    boolean writeFile(Workbook workbook, String filename) throws IOException;

    boolean writeFile(String content, String filename) throws IOException;

    ArrayList<DReport> getReportFile(String docPath) throws Exception;

    void logs(String text) throws Exception;

    void deleteFile(String docPath) throws Exception;
}
