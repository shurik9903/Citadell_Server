package org.example.model.connections;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.example.data.mydata.DUserConnect;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.FileUtils;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class UserConnections implements IUserConnections {

    @Inject
    private IFileUtils fileUtils;

    @Override
    public DUserConnect.Analysis getAnalysisFile(String uuid, String userLogin) throws Exception{
        try {

            Jsonb jsonb = JsonbBuilder.create();
            String path = ServerProperties.getProperty("filepath");
            String fileName = ServerProperties.getProperty("connectFileName");

            File file = new File(path + "/" + fileName);
            if (!file.exists()) {
                return null;
            }

            FileInputStream jsonFile = new FileInputStream(path + "/" + fileName);

            String jsonText = new String(jsonFile.readAllBytes());

            jsonFile.close();

            ArrayList<DUserConnect> userConnects =  new ArrayList<>(Arrays.asList(jsonb.fromJson(jsonText, DUserConnect[].class)));

            Optional<DUserConnect> userConnectOption = userConnects.stream().filter(dUserConnect ->
                dUserConnect.getUserLogin().equals(userLogin)
            ).findFirst();

            if (userConnectOption.isEmpty()){
                return null;
            }

            Optional<DUserConnect.Analysis> analysisOption = userConnectOption.get().getAnalysis().stream().filter(analysis -> analysis.getUuid().equals(uuid)).findFirst();

            if (analysisOption.isEmpty()){
                return null;
            }

            return analysisOption.get();
        } catch (Exception e){
            throw new Exception("Ошибка при чтении JSON файла");
        }
    }


    @Override
    public ArrayList<DUserConnect> getUserConnect() throws  Exception {
        try {

            Jsonb jsonb = JsonbBuilder.create();
            String path = ServerProperties.getProperty("filepath");
            String fileName = ServerProperties.getProperty("connectFileName");

            File file = new File(path + "/" + fileName);
            if (!file.exists()) {
                if (! fileUtils.writeFile("[]",path + "/" + fileName)) throw new Exception("Ошибка при работе с файлом: " + fileName);
            }

            FileInputStream jsonFile = new FileInputStream(path + "/" + fileName);

            String jsonText = new String(jsonFile.readAllBytes());

            jsonFile.close();

            DUserConnect[] userConnects = jsonb.fromJson(jsonText, DUserConnect[].class);

            return new ArrayList<>(Arrays.asList(userConnects));
        } catch (Exception e){
            throw new Exception("Ошибка при чтении JSON файла");
        }
    }

    @Override
    public void saveUserConnect(String userConnectJSON) throws  Exception {
        try {

            String path = ServerProperties.getProperty("filepath");
            String fileName = ServerProperties.getProperty("connectFileName");

            File file = new File(path + "/" + fileName);
            if (!file.exists()) {
                if (! fileUtils.writeFile("[]",path + "/" + fileName)) throw new Exception("Ошибка при работе с файлом: " + fileName);
            }

            PrintWriter out = new PrintWriter(new FileWriter(file));

            out.write(userConnectJSON);
            out.flush();
            out.close();

        } catch (Exception e){
            throw new Exception("Ошибка при чтении JSON файла");
        }
    }

    @Override
    public void setUserData(String sessionID, String userLogin) throws Exception {
        Jsonb jsonb = JsonbBuilder.create();

        FileUtils fileUtils = new FileUtils();

        ArrayList<DUserConnect> userConnects = getUserConnect();

        Optional<DUserConnect> findUser = userConnects.stream()
                .filter(dUserConnect -> dUserConnect.getUserLogin().equals(userLogin)).findFirst();

        if (findUser.isPresent()){
            findUser.get().setConnectID(sessionID);
        } else {
            DUserConnect userConnect = new DUserConnect();
            userConnect.setUserLogin(userLogin);
            userConnect.setConnectID(sessionID);
            userConnects.add(userConnect);
        }

        saveUserConnect(jsonb.toJson(userConnects));
    }


    @Override
    public void delUserData(String sessionID) throws Exception {
        Jsonb jsonb = JsonbBuilder.create();

        FileUtils fileUtils = new FileUtils();

        ArrayList<DUserConnect> userConnects = getUserConnect();

        userConnects.removeIf(dUserConnect -> dUserConnect.getConnectID().equals(sessionID));

        saveUserConnect(jsonb.toJson(userConnects));
    }
}
