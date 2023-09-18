package org.example.model.teaching;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DAnalysisResult;
import org.example.data.mydata.DTeaching;
import org.example.data.mydata.DUserConnect;
import org.example.model.ML.thread.TWaitResultAnalysis;
import org.example.model.connections.IUserConnections;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Teaching implements ITeaching{

    @Inject
    private IUserConnections userConnections;

    @Inject
    private IFileUtils fileUtils;

    @Override
    public Response startTeaching(String json, String userLogin, String userID) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();


            DTeaching teaching =  jsonb.fromJson(json, DTeaching.class);


            if (teaching.getName().isEmpty() || teaching.getColumn().isEmpty() || teaching.getSelect().isEmpty() || teaching.getModelID().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка данных, отсутствует название файла, номер столбца или номер модели").build();
            }

            IDocReader docReader = DocReaderFactory.getDocReader(teaching.getName().substring(teaching.getName().lastIndexOf('.')));
            ArrayList<Map<String, Object>> columns = docReader.parserSelectColumn(
                    ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + teaching.getName(),
                    Integer.parseInt(teaching.getColumn()),
                    Integer.parseInt(teaching.getSelect()));

            Map<Object, Object> teachingData = new HashMap<>();
            teachingData.put("userID", userID);
            teachingData.put("modelID", teaching.getModelID());
            teachingData.put("comments", columns);

            System.out.println("teaching " + columns);
            System.out.println("teaching data " + teachingData);

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/teach");

            request.setBody(teachingData);

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            Map<String, String> data = new HashMap<>();

            data = (Map<String, String>) jsonb.fromJson(result, data.getClass());

            String uuid = data.getOrDefault("UUID", "");


            if (uuid.isEmpty()) {
                jsonOut.put("msg", "Ошибка данных, uuid не выделен.");
                return Response.ok(jsonb.toJson(jsonOut)).build();
            }

            new Thread(){
                @Override
                public void run() {
                    super.run();

                    try {
                        TWaitResultAnalysis tWaitResultAnalysis = new TWaitResultAnalysis(userLogin + "|" + teaching.getName(), uuid, "teach");

                        tWaitResultAnalysis.getThread().start();
                        tWaitResultAnalysis.getThread().join();

                        OutMessage outMessage = new OutMessage();
                        outMessage.setLogin(userLogin);
                        outMessage.setType("FileTeachResult");
                        outMessage.setMessage(jsonb.toJson(new HashMap<>(){{
                            put("uuid", uuid);
                            put("fileName", teaching.getName());
                        }}));

                        DUserConnect.Analysis analysis = new DUserConnect.Analysis();

                        analysis.setUuid(uuid);
                        analysis.setFileName(teaching.getName());
                        analysis.setStatus(false);
                        new WebSocket().sendResultMessage(outMessage, userLogin, analysis);

                    }catch (Exception e){
                        System.out.println("Ошибка " + e.getMessage());
                    }
                }
            }.start();



            DUserConnect.Analysis userAnalysisData = new DUserConnect.Analysis();
            userAnalysisData.setFileName(teaching.getName());
            userAnalysisData.setUuid(uuid);
            userAnalysisData.setStatus(false);
            subscribeFile(userAnalysisData, userLogin);

            return Response.ok(jsonb.toJson(jsonOut)).build();

        } catch (Exception e){
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getTeachingStatus(String uuid) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            if (uuid == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Не указан uuid анализируемых данных").build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/teach/status/" + uuid);

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            return Response.ok(result).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getTeachingResult(String uuid, String userLogin) {
        try {

            Jsonb jsonb = JsonbBuilder.create();

            if (uuid == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Не указан uuid анализируемых данных").build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/teach/result/" + uuid);

            String result = request.send();

            System.out.println("result " + result);
            DAnalysisResult data = jsonb.fromJson(result, DAnalysisResult.class);

            String message = data.getMessage();

            if (message != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
            }

            DUserConnect.Analysis analysis = userConnections.getAnalysisFile(uuid, userLogin);

            if (analysis == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("файл с данным uuid не найден " + uuid).build();
            }

            String fileName = analysis.getFileName();

            fileUtils.logs(result);

            IDocReader docReader = DocReaderFactory.getDocReader(fileName.substring(fileName.lastIndexOf('.')));
            docReader.setDataAnalysis(data, ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName);

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            return Response.ok(result).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    private void subscribeFile(DUserConnect.Analysis analysis, String userLogin) throws Exception {

        Jsonb jsonb = JsonbBuilder.create();

        ArrayList<DUserConnect> userConnects = userConnections.getUserConnect();

        Optional<DUserConnect> findUser = userConnects.stream()
                .filter(dUserConnect -> dUserConnect.getUserLogin().equals(userLogin)).findFirst();

        if (findUser.isPresent()) {

            Optional<DUserConnect.Analysis> selectFile = findUser.get().getAnalysis().stream().filter(value ->
                    value.getFileName().equals(analysis.getFileName())
            ).findFirst();

            if (selectFile.isPresent()) {
                selectFile.get().setUuid(analysis.getUuid());
            } else {
                findUser.get().getAnalysis().add(analysis);
            }
        } else {
            DUserConnect userConnect = new DUserConnect();
            userConnect.setUserLogin(userLogin);
            userConnect.getAnalysis().add(analysis);
            userConnects.add(userConnect);
        }

        userConnections.saveUserConnect(jsonb.toJson(userConnects));

    }
}
