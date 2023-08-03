package org.example.model.database.dictionaryWork;

import org.example.data.entity.ESimpleWord;
import org.example.data.entity.ESpellingVariants;
import org.example.data.entity.ETypeWord;
import org.example.model.database.IDBWork;

import java.util.ArrayList;

public interface IDBDictionaryWork extends IDBWork {

    ESimpleWord loadSimpleWord(String wordID) throws Exception;

    ArrayList<ESimpleWord> loadSimpleWords() throws Exception;

    void saveSimpleWord(String word, int typeID) throws Exception;

    void deleteSimpleWord(int wordID) throws Exception;

    ESpellingVariants loadSpellingWord(String wordID) throws Exception;

    ArrayList<ESpellingVariants> loadSpellingWords() throws Exception;

    void saveSpellingWord(String word, int simpleID) throws Exception;

    void deleteSpellingWord(int wordID) throws Exception;

    ArrayList<ETypeWord> loadTypeWords() throws Exception;

    void updateSimpleWord(int wordID, String word, int typeID) throws Exception;

    void updateSpellingWord(int wordID, String word, int simpleID) throws Exception;
}
