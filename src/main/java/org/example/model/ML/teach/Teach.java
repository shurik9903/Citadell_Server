package org.example.model.ML.teach;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class Teach implements ITeach{

    @Override
    public Response getTeachStatus(String uuid){
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();


            if (uuid == null) {
                jsonOut.put("msg", "Не указан uuid обучающей модели");
                Response.ok(jsonb.toJson(jsonOut)).build();
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
    public Response getTeachResult(String uuid){
        try {
            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> jsonOut = new HashMap<>();

            if (uuid == null) {
                jsonOut.put("msg", "Не указан uuid обучающей модели");
                Response.ok(jsonb.toJson(jsonOut)).build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/teach/result/" + uuid);

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
    public Response setTeach(String json){
        try {
            Jsonb jsonb = JsonbBuilder.create();

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/teach");

            request.setBody(jsonb.fromJson(json, HashMap.class));

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
