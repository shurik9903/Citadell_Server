package org.example.model.workingFiles;

import jakarta.ws.rs.core.Response;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;

public interface IWorkingFiles {
    Response saveFile(String document, String userID, String userLogin);

    Response overwriteFile(String doc_name, String userid, String userLogin);

    Response loadFile();
}
