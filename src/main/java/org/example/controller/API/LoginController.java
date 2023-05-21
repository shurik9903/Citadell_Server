package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.login.ILogin;

@Path("/login")
public class LoginController {

    @Inject
    private ILogin log;

    @POST
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    public Response doPost(String jsonData) {
        try {
            return log.loginFunc(jsonData);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }
}
