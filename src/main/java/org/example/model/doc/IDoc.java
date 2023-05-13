package org.example.model.doc;

import jakarta.ws.rs.core.Response;

public interface IDoc {
    Response loadDoc(String name);

    Response allDocs(String userID);

    Response readDoc(String docName, int start, int diapason, String userid, String userLogin);

    Response updateDoc(String fileName, String docData, String userLogin, String userID);
}
