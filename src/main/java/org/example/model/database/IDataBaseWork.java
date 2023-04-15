package org.example.model.database;


import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.EFile;
import org.example.data.entity.ELogin;

public interface IDataBaseWork {
    ELogin login(String login, String Password);

    String saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace);

    String overwriteFile(String fileName, byte[] fileByte, String userID);

    EFile loadFile(String fileName, String userID);

    boolean ping();

}
