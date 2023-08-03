package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;
import org.example.model.ML.models.IModels;
import org.example.model.doc.IDoc;
import org.example.model.token.TokenRequired;

@Path("/models")
public class ModelsController {

    @Inject
    private IModels models;

    @GET
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet() {
        try {
            return models.getModels();
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doPut(String modelID) {
        try {
            return models.setModels(modelID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{model_id}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doDelete(@PathParam("model_id") String modelID) {
        try {
            return models.deleteModels(modelID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}