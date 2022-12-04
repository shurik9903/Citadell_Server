package org.example.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/ping")
public class Ping {

    @GET
    @Produces("application/json")
    public Response doGet(@PathParam("word") String word, @HeaderParam("Token") String UserToken) {
        try {
            return Response.ok("OK").build();
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }
}

