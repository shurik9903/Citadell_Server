package org.example.model.workingFiles;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;

public interface IWorkingFiles {
    boolean writeFile(byte[] content, String filename) throws IOException;

    boolean writeFile(Workbook workbook, String filename) throws IOException;
}
