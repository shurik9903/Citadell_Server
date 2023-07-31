package org.example.model.doc.docReader;

import java.util.ArrayList;
import java.util.Map;

public interface IDocReader {
    String getFullName();

    void setDoc(String data);

    void saveFile(String savePath) throws Exception;
    void setDataAnalysis(Object data, String docPath) throws Exception;

    void updateDoc(String docPath, String docData, String userID) throws Exception;

    String parser(String loadPath) throws Exception;

    ArrayList<Map<String, Object>> parserSelectColumn(String loadPath, int column, int select) throws Exception;

    String parser(String loadPath, int start, int number) throws Exception;
}
