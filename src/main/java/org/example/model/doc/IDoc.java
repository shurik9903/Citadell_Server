package org.example.model.doc;

import jakarta.ws.rs.core.Response;

public interface IDoc {
    Response loadDoc(String name);
}
