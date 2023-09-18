package org.example.model.ML.models;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Models implements IModels {

    @Override
    public Response getModels(){
        try {
            Jsonb jsonb = JsonbBuilder.create();

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/models");

            String currentModel = request.send();
            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(currentModel).build();

            request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/models/list");

            String allModels = request.send();
            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(allModels).build();

            Map<String, String> united = new HashMap<>();

            united.putAll( jsonb.fromJson(currentModel, united.getClass()));
            united.putAll( jsonb.fromJson(allModels, united.getClass()));

            return Response.ok(jsonb.toJson(united)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response setModels(String data){
        try {

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            Map<String, String> jsonParse = new HashMap<>();
            jsonParse = (Map<String, String>) jsonb.fromJson(data, jsonParse.getClass());

            String id = String.valueOf(jsonParse.getOrDefault("ModelID", ""));

            if (id.isEmpty()) {
                jsonOut.put("msg", "Не указан id модели");
                Response.ok(jsonb.toJson(jsonOut)).build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.PUT, "api/v1/models");

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
    public Response deleteModels(String id){
        try {

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();


            if (id == null) {
                jsonOut.put("msg", "Не указан id удаляемой модели");
                Response.ok(jsonb.toJson(jsonOut)).build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.DELETE, "api/v1/models/" + id);

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
    public Response updateModel(int modelID, String name) {
        try {

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            Map<String, String> jsonParse = new HashMap<>();
            jsonParse = (Map<String, String>) jsonb.fromJson(name, jsonParse.getClass());

            String newName = String.valueOf(jsonParse.getOrDefault("NameModel", ""));

            if (newName.isEmpty())
                throw new Exception("Не указано новое имя модели");

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.PUT, "api/v1/models/"+modelID);
            request.setBody(jsonb.fromJson(name, HashMap.class));

            String result = request.send();

            if (request.responseCode != 200)
                return Response.status(request.responseCode).entity(result).build();

            return Response.ok(result).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
