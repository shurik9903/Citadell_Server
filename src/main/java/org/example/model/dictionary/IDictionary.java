package org.example.model.dictionary;

import jakarta.ws.rs.core.Response;

public interface IDictionary {

    Response loadSimpleWord(String wordID);

    Response saveSimpleWord(String word);

    Response deleteSimpleWord(int wordID);

    Response loadSpellingWord(String wordID);

    Response loadSimpleWords();

    Response loadSpellingWords();

    Response saveSpellingWord(String word);

    Response loadTypeWords();

    Response updateSimpleWord(String word);

    Response updateSpellingWord(String word);

    Response deleteSpellingWord(int wordID);
}
