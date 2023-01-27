package org.example.model.database;


import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.mydata.DFile;

public interface IDataBaseWork {
    Object login(String login, String Password);

    String saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace);

    String overwriteFile(String fileName, byte[] fileByte, String userID);

    DFile loadFile(String fileName, String userID);

    boolean ping();

}
