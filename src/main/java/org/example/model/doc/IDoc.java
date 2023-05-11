package org.example.model.doc;

import jakarta.ws.rs.core.Response;

public interface IDoc {
    Response loadDoc(String name);

    Response allDocs(String userID);

    Response readDoc(String doc_name, int start, int diapason, String userid, String userLogin);

    Response updateDoc(String docData, String userLogin);
}
