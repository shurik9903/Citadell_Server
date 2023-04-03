package org.example.model.doc;

import jakarta.ws.rs.core.Response;

public interface IDoc {
    Response loadDoc(String name);

    Response saveFile(String document);

    Response overwriteFile(String doc_name, String userid);

    Response loadFile();

    Response readDoc(String doc_name, int start, int diapason, String userid);
}
