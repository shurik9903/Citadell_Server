package org.example.model.teaching;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DAnalysis;
import org.example.data.mydata.DTeaching;
import org.example.data.mydata.DUserConnect;
import org.example.model.ML.thread.TWaitResult;
import org.example.model.connections.IUserConnections;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


            if (teaching.getName().isEmpty() || teaching.getColumn().isEmpty() || teaching.getSelect().isEmpty()){
                jsonOut.put("msg", "Ошибка данных, отсутствует название файла или номер столбца");
                return Response.ok(jsonb.toJson(jsonOut)).build();
            }

            IDocReader docReader = DocReaderFactory.getDocReader(teaching.getName().substring(teaching.getName().lastIndexOf('.')));
            ArrayList<Map<String, Object>> columns = docReader.parserSelectColumn(
                    ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + teaching.getName(),
                    Integer.parseInt(teaching.getColumn()),
                    Integer.parseInt(teaching.getSelect()));

            Map<Object, Object> predictData = new HashMap<>();
            predictData.put("userID", userID);
            predictData.put("comments", columns);

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/teach");

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

            new TWaitResult(userLogin+"|"+teaching.getName(), uuid, teaching.getName(), userLogin);


            DUserConnect.Analysis userAnalysisData = new DUserConnect.Analysis();
            userAnalysisData.setFileName(teaching.getName());
            userAnalysisData.setUuid(uuid);
            userAnalysisData.setStatus(false);
//            subscribeFile(userAnalysisData, userLogin);

            return Response.ok(jsonb.toJson(jsonOut)).build();

        } catch (Exception e){
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }
}
