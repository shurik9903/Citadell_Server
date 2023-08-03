package org.example.controller.API;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;


@Path("/ping")
public class Ping {

    @GET
    @Produces("application/json; charset=UTF-8")
    public Response doGet() {
        try {
            return Response.ok("OK").build();
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}



