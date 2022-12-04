package org.example.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.login.ILogin;

@Path("/login")
public class LoginController {

    @Inject
    private ILogin log;

    @GET
    @Path("/ping")
    public String ping() {
        return "Ping";
    }

    @GET
    @Path("{login:.*}/{password:.*}")
    @Produces("application/json")
    public Response doGet(@PathParam("login") String login, @PathParam("password") String password, @HeaderParam("Token") String UserToken) {
        try {
            return log.LoginFunc(login, password);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }
}
