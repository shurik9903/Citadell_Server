package org.example.model.analysis;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;
import jakarta.ws.rs.core.Response;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.model.database.IDataBaseWork;
import org.example.model.doc.IDoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Analysis implements IAnalysis {

    @Inject
    private IDataBaseWork DataBaseWork;

    @Override
    public Response loadAnalysis(String docid){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();


        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server.");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            OutMessage outMessage = new OutMessage();
            outMessage.setType("MSG");
            outMessage.setMessage("ID TEST");

            new WebSocket().sendMessage(outMessage);

            int min = 0;
            int max = 100;

            Result.put("id_file", Integer.toString(new Random().nextInt((max - min) + 1) + min));
            Result.put("Msg", "");

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }

    }

}
