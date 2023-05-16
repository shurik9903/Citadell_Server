package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.ML.predict.IPredict;
import org.example.model.ML.teach.ITeach;
import org.example.model.token.TokenRequired;

@Path("/teach")
public class TeachController {

    @Inject
    private ITeach teach;

    @GET
    @Path("/status/{uuid}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetStatus(@PathParam("uuid") String uuid) {
        try {
            return teach.getTeachStatus(uuid);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/result/{uuid}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetResult(@PathParam("uuid") String uuid) {
        try {
            return teach.getTeachResult(uuid);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doPut(String json) {
        try {
            return teach.setTeach(json);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

}
