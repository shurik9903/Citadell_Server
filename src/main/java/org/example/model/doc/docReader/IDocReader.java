package org.example.model.doc.docReader;

import org.example.data.mydata.DExcel;

import java.io.IOException;
import java.util.ArrayList;

public interface IDocReader {
    String getFullName();

    void setDoc(String data);

    void saveFile(String savePath) throws Exception;

    void updateDoc(String docPath, String docData) throws IOException;

    String parser(String loadPath, int start, int number) throws Exception;
}
