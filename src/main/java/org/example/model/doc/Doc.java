package org.example.model.doc;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.data.mydata.DLogin;
import org.example.model.database.IDataBaseWork;
import org.example.model.token.ITokenIssuer;
import org.example.model.token.ITokenKey;
import org.example.model.token.TokenIssuer;
import org.example.model.token.TokenKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Doc implements IDoc{

    @Inject
    private IDataBaseWork DataBaseWork;

    @Override
    public Response loadDoc(String name){
        Jsonb jsonb = JsonbBuilder.create();

        Map<String, String> Result = new HashMap<>();


        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server.");
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
