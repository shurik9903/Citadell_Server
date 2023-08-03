package org.example.model.workingFiles;

import jakarta.ws.rs.core.Response;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;

public interface IWorkingFiles {
    Response saveFile(String document, String userID, String userLogin);

    Response overwriteFile(String docName, String userID, String userLogin);

    Response loadFile(String userID, String userLogin, String docName);

    Response deleteFile(String fileName, String userLogin, String userID);
}
