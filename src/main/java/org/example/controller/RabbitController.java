package org.example.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.messageBroker.IMessBrok;

@Path("/test")
public class RabbitController {

    @Inject
    private IMessBrok messBrok;

    @GET
    @Path("/ping")
    public String ping() {
        return "Ping";
    }

    //Подписаться
    @GET
    @Produces("application/json")
    public Response doGet(@HeaderParam("Token") String UserToken) {
        try {
            return messBrok.consume();
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

    //Объявить
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response doPost(String json, @HeaderParam("Token") String UserToken) {
        try {
            return messBrok.declare(json);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

    //Отправить сообщение
    @PUT
    @Produces("application/json")
    public Response doPut(String json, @HeaderParam("Token") String UserToken) {
        try {
            return messBrok.send(json);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }
}

