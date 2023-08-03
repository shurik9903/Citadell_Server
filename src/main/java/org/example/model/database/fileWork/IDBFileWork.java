package org.example.model.database.fileWork;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.EFile;
import org.example.data.entity.ENameFiles;
import org.example.model.database.IDBWork;

import java.util.ArrayList;

public interface IDBFileWork extends IDBWork {
    void saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace) throws Exception;

    void overwriteFile(String fileName, byte[] fileByte, String userID) throws Exception;

    ArrayList<ENameFiles> allFiles(String userID) throws Exception;

    EFile loadFile(String fileName, String userID) throws Exception;

    void deleteFile(String fileName, String userID) throws Exception;
}
