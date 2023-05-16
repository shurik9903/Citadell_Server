package org.example.model.analysis;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;
import jakarta.ws.rs.core.Response;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DUserConnect;
import org.example.model.ML.thread.TWaitResult;
import org.example.model.database.IDataBaseWork;
import org.example.model.doc.IDoc;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.FileUtils;

import java.io.File;
import java.util.*;

public class Analysis implements IAnalysis {

    @Inject
    private IDataBaseWork DataBaseWork;

//    @Override
//    public Response loadAnalysis(String docid){
//        Jsonb jsonb = JsonbBuilder.create();
//        Map<String, String> Result = new HashMap<>();
//
//
//        try {
//            if (!DataBaseWork.ping()) {
//                Result.put("Msg", "Нет соединения с базой данных");
//                return Response.ok(jsonb.toJson(Result)).build();
//            }
//
//            OutMessage outMessage = new OutMessage();
//            outMessage.setType("MSG");
//            outMessage.setMessage("ID TEST");
//
//            new WebSocket().sendMessage(outMessage);
//
//            int min = 0;
//            int max = 100;
//
//            Result.put("id_file", Integer.toString(new Random().nextInt((max - min) + 1) + min));
//            Result.put("Msg", "");
//
//            return Response.ok(jsonb.toJson(Result)).build();
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
//        }
//
//    }

    @Override
    public Response startAnalysis(String json, String userLogin, String userID){
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            Map<String, String> data = new HashMap<>();

            data =  (Map<String, String>) jsonb.fromJson(json, data.getClass());

            String fileName = data.getOrDefault("name", "");
            String column = data.getOrDefault("column", "");

            if (fileName.isEmpty() || column.isEmpty()){
                jsonOut.put("msg", "Ошибка данных, отсутствует название файла или номер столбца");
                return Response.ok(jsonb.toJson(jsonOut)).build();
            }

            IDocReader docReader = DocReaderFactory.getDocReader(fileName.substring(fileName.lastIndexOf('.')));
            ArrayList<Map<String, Object>> columns = docReader.parser(ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName, Integer.parseInt(column));

            Map<Object, Object> predictData = new HashMap<>();
            predictData.put("userID", userID);
            predictData.put("comments", columns);

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict");

            request.setBody(predictData);

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            data = (Map<String, String>) jsonb.fromJson(result, data.getClass());

            String uuid = data.getOrDefault("UUID", "");


            if (uuid.isEmpty()) {
                jsonOut.put("msg", "Ошибка данных, uuid не выделен.");
                return Response.ok(jsonb.toJson(jsonOut)).build();
            }

            new TWaitResult(userLogin+"|"+fileName, uuid, fileName, userLogin);


            DUserConnect.Analysis analysis = new DUserConnect.Analysis();
            analysis.setFileName(fileName);
            analysis.setUuid(uuid);
            analysis.setStatus(false);
            subscribeFile(analysis, userLogin);

            return Response.ok(jsonb.toJson(jsonOut)).build();

        } catch (Exception e){
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response getAnalysisStatus(String uuid){
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            if (uuid == null) {
                jsonOut.put("msg", "Не указан uuid анализируемых данных");
                Response.ok(jsonb.toJson(jsonOut)).build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict/status/" + uuid);

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            return Response.ok(result).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response getAnalysisResult(String uuid){
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            if (uuid == null) {
                jsonOut.put("msg", "Не указан uuid анализируемых данных");
                Response.ok(jsonb.toJson(jsonOut)).build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict/result/" + uuid);

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            return Response.ok(result).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    private void subscribeFile(DUserConnect.Analysis analysis, String userLogin) throws Exception {

        Jsonb jsonb = JsonbBuilder.create();

        FileUtils fileUtils = new FileUtils();

        ArrayList<DUserConnect> userConnects = fileUtils.getUserConnect();

        Optional<DUserConnect> findUser = userConnects.stream()
                .filter(dUserConnect -> dUserConnect.getUserLogin().equals(userLogin)).findFirst();

        if (findUser.isPresent()){
            findUser.get().getAnalysis().add(analysis);
        } else {
            DUserConnect userConnect = new DUserConnect();
            userConnect.setUserLogin(userLogin);
            userConnect.getAnalysis().add(analysis);
            userConnects.add(userConnect);
        }

        fileUtils.saveUserConnect(jsonb.toJson(userConnects));

    }

}
