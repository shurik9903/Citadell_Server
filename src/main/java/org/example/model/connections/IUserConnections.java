package org.example.model.connections;

import org.example.data.mydata.DUserConnect;

import java.util.ArrayList;

public interface IUserConnections {
    DUserConnect.Analysis getAnalysisFile(String uuid, String userLogin) throws Exception;

    ArrayList<DUserConnect> getUserConnect() throws  Exception;

    void saveUserConnect(String userConnectJSON) throws  Exception;

    void setUserData(String sessionID, String userLogin) throws Exception;

    void delUserData(String sessionID) throws Exception;
}
