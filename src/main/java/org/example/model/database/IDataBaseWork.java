package org.example.model.database;


import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.ENameFiles;
import org.example.data.entity.EFile;
import org.example.data.entity.ELogin;
import org.example.data.mydata.DReport;

import java.util.ArrayList;

public interface IDataBaseWork {
    ELogin login(String login, String Password);

    String saveReports(String fileName, String userID, ArrayList<DReport> reports);

    String saveFile(String fileName, byte[] fileByte, String userID, MutableBoolean replace);

    String overwriteFile(String fileName, byte[] fileByte, String userID);

    ArrayList<ENameFiles> allFiles(String userID, StringBuilder msg);

    EFile loadFile(String fileName, String userID);

    boolean ping();

}
