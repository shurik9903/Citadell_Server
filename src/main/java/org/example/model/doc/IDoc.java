package org.example.model.doc;

import jakarta.ws.rs.core.Response;

public interface IDoc {
    Response loadDoc(String name);

    Response allDocs(String userID);

    Response saveFile(String document, String userID, String userLogin);

    Response overwriteFile(String doc_name, String userid, String userLogin);

    Response loadFile();

    Response readDoc(String doc_name, int start, int diapason, String userid, String userLogin);
}
