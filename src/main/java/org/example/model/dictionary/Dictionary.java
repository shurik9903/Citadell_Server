package org.example.model.dictionary;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.model.database.IDataBaseWork;


import java.util.HashMap;
import java.util.Map;

public class Dictionary implements IDictionary {
    @Inject
    private IDataBaseWork DataBaseWork;

    @Override
    public Response loadWord(String word){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();


        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server..");
                return Response.ok(jsonb.toJson(Result)).build();
            }


            Result.put("Msg", "");
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }

    }

}

