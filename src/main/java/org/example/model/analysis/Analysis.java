package org.example.model.analysis;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DAnalysis;
import org.example.data.mydata.DAnalysisResult;
import org.example.data.mydata.DUserConnect;
import org.example.model.ML.thread.TWaitDictionaryWordsFind;
import org.example.model.ML.thread.TWaitResultAnalysis;
import org.example.model.connections.IUserConnections;
import org.example.model.database.dictionaryWork.IDBDictionaryWork;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.util.*;

public class Analysis implements IAnalysis {

    @Inject
    private IDBDictionaryWork dataBase;

    @Inject
    private IUserConnections userConnections;

    @Inject
    private IFileUtils fileUtils;

    @Override
    public Response startAnalysis(String json, String userLogin, String userID) {
        try {

            dataBase.ping();

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            DAnalysis analysis = jsonb.fromJson(json, DAnalysis.class);

            if (analysis.getName().isEmpty() || analysis.getColumn().isEmpty() || analysis.getSelect().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка данных, отсутствует название файла или номер столбца").build();
            }

            String docType = analysis.getName().substring(analysis.getName().lastIndexOf('.'));
            String pathToFile = ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + analysis.getName();

            IDocReader docReader = DocReaderFactory.getDocReader(docType);
            ArrayList<Map<String, Object>> columns = docReader.parserSelectColumn(
                    pathToFile,
                    Integer.parseInt(analysis.getColumn()),
                    Integer.parseInt(analysis.getSelect()));

            Map<Object, Object> predictData = new HashMap<>();
            predictData.put("userID", userID);
            predictData.put("comments", columns);

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict");

            request.setBody(predictData);

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            Map<String, String> data = new HashMap<>();

            data = (Map<String, String>) jsonb.fromJson(result, data.getClass());

            String uuid = data.getOrDefault("UUID", "");


            if (uuid.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка данных, uuid не выделен.").build();
            }

            new Thread() {
                @Override
                public void run() {
                    super.run();

                    try {
                        TWaitResultAnalysis tWaitResultAnalysis = new TWaitResultAnalysis(userLogin + "|" + analysis.getName(), uuid, "predict");

                        TWaitDictionaryWordsFind tWaitDictionaryWordsFind = new TWaitDictionaryWordsFind(userLogin + "|" + analysis.getName(), columns, dataBase.loadSpellingWords(), (docData) -> {
                            try {
                                docReader.updateDoc(pathToFile, docData, userID);
                            } catch (Exception e) {
                                System.out.println("Ошибка: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });

                        tWaitResultAnalysis.getThread().start();
                        tWaitDictionaryWordsFind.getThread().start();

                        tWaitResultAnalysis.getThread().join();
                        tWaitDictionaryWordsFind.getThread().join();

                        OutMessage outMessage = new OutMessage();
                        outMessage.setLogin(userLogin);
                        outMessage.setType("FilePredictResult");
                        outMessage.setMessage(jsonb.toJson(new HashMap<>() {{
                            put("uuid", uuid);
                            put("fileName", analysis.getName());
                        }}));

                        DUserConnect.Analysis statusAnalysis = new DUserConnect.Analysis();

                        statusAnalysis.setUuid(uuid);
                        statusAnalysis.setFileName(analysis.getName());
                        statusAnalysis.setStatus(false);
                        new WebSocket().sendResultMessage(outMessage, userLogin, statusAnalysis);

                    } catch (Exception e) {
                        System.out.println("Ошибка " + e.getMessage());
                    }

                }
            }.start();

            DUserConnect.Analysis userAnalysisData = new DUserConnect.Analysis();
            userAnalysisData.setFileName(analysis.getName());
            userAnalysisData.setUuid(uuid);
            userAnalysisData.setStatus(false);
            subscribeFile(userAnalysisData, userLogin);

            return Response.ok(jsonb.toJson(jsonOut)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getAnalysisStatus(String uuid) {
        try {
            if (uuid == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Не указан uuid анализируемых данных").build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/predict/status/" + uuid);

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
    public Response getAnalysisResult(String uuid, String userLogin) {
        try {

            Jsonb jsonb = JsonbBuilder.create();

            if (uuid == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Не указан uuid анализируемых данных").build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/predict/result/" + uuid);

            String result = request.send();
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
