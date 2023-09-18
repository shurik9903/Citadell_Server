package org.example.model.ML.predict;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class Predict implements IPredict{

    @Override
    public Response getPredictStatus(String uuid){
        try {

            if (uuid == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Не указан uuid анализируемых данных").build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict/status/" + uuid);

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
    public Response getPredictResult(String uuid){
        try {
            if (uuid == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Не указан uuid анализируемых данных").build();
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict/result/" + uuid);

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
    public Response setPredict(String json){
        try {
            Jsonb jsonb = JsonbBuilder.create();

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict");

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
