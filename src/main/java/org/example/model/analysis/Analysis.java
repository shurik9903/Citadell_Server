package org.example.model.analysis;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;
import jakarta.ws.rs.core.Response;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DAnalysis;
import org.example.data.mydata.DAnalysisResult;
import org.example.data.mydata.DReport;
import org.example.data.mydata.DUserConnect;
import org.example.model.ML.thread.TWaitResult;
import org.example.model.connections.IUserConnections;
import org.example.model.database.IDataBaseWork;
import org.example.model.doc.IDoc;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.FileUtils;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Analysis implements IAnalysis {

    @Inject
    private IUserConnections userConnections;

    @Inject
    private IFileUtils fileUtils;

    @Override
    public Response startAnalysis(String json, String userLogin, String userID){
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();


            DAnalysis analysis =  jsonb.fromJson(json, DAnalysis.class);


            if (analysis.getName().isEmpty() || analysis.getColumn().isEmpty() || analysis.getSelect().isEmpty()){
                jsonOut.put("msg", "Ошибка данных, отсутствует название файла или номер столбца");
                return Response.ok(jsonb.toJson(jsonOut)).build();
            }

            IDocReader docReader = DocReaderFactory.getDocReader(analysis.getName().substring(analysis.getName().lastIndexOf('.')));
            ArrayList<Map<String, Object>> columns = docReader.parserSelectColumn(
                    ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + analysis.getName(),
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
                jsonOut.put("msg", "Ошибка данных, uuid не выделен.");
                return Response.ok(jsonb.toJson(jsonOut)).build();
            }

            new TWaitResult(userLogin+"|"+analysis.getName(), uuid, analysis.getName(), userLogin);


            DUserConnect.Analysis userAnalysisData = new DUserConnect.Analysis();
            userAnalysisData.setFileName(analysis.getName());
            userAnalysisData.setUuid(uuid);
            userAnalysisData.setStatus(false);
            subscribeFile(userAnalysisData, userLogin);

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

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/predict/status/" + uuid);

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
    public Response getAnalysisResult(String uuid, String userLogin){
        try {

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            if (uuid == null) {
                jsonOut.put("msg", "Не указан uuid анализируемых данных");
                Response.ok(jsonb.toJson(jsonOut)).build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/predict/result/" + uuid);

            String result = request.send();
            DAnalysisResult data = jsonb.fromJson(result, DAnalysisResult.class);

            String message = data.getMessage();

            if (message != null){
                return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + message).build();
            }

            DUserConnect.Analysis analysis = userConnections.getAnalysisFile(uuid, userLogin);

            if (analysis == null){
                return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: файл с данным uuid не найден " + uuid).build();
            }


            String fileName = analysis.getFileName();

//            ArrayList<DReport> reports = fileUtils.getReportFile(ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName);

//            Set<String> unavailableItems = data.getComments().stream()
//                    .map(analysisRows ->  String.valueOf(analysisRows.getNumber()))
//                    .collect(Collectors.toSet());
//
//
//            List<DReport> unavailable = reports.stream()
//                    .filter(e -> unavailableItems.contains(e.getRowNum()))
//                    .collect(Collectors.toList());

//            data.getComments().forEach(analysisRows -> {
//
//                Optional<DReport> report = reports.stream().filter(dReport -> dReport.getRowNum().equals(String.valueOf(analysisRows.getNumber()))).findFirst();
//
//                if (report.isPresent()){
//                   report.get().set
//                }
//
//            });


//            reports.stream().filter(dReport -> dReport.);
            fileUtils.logs(result);

            IDocReader docReader = DocReaderFactory.getDocReader(fileName.substring(fileName.lastIndexOf('.')));
            docReader.setDataAnalysis(data, ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName);

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

        ArrayList<DUserConnect> userConnects = userConnections.getUserConnect();

        Optional<DUserConnect> findUser = userConnects.stream()
                .filter(dUserConnect -> dUserConnect.getUserLogin().equals(userLogin)).findFirst();

        if (findUser.isPresent()){

            Optional<DUserConnect.Analysis> selectFile = findUser.get().getAnalysis().stream().filter(value ->
                value.getFileName().equals(analysis.getFileName())
             ).findFirst();

            if (selectFile.isPresent()){
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
