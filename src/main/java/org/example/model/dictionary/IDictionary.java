package org.example.model.dictionary;

import jakarta.ws.rs.core.Response;

public interface IDictionary {
    Response loadWord(String word);
}
