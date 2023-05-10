package org.example.model.doc.docReader;

public interface IDocReader {
    String getFullName();

    void setDoc(String data);

    void saveFile(String userName) throws Exception;
}
