package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.ML.predict.IPredict;
import org.example.model.analysis.IAnalysis;
import org.example.model.token.*;

@Path("/analysis")
public class AnalysisController {

    @Inject
    private IAnalysis analysis;

    @POST
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet(String document, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return analysis.startAnalysis(document, userLogin, userID);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/status/{uuid}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetStatus(@PathParam("uuid") String uuid) {
        try {
            return analysis.getAnalysisStatus(uuid);
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
            return analysis.getAnalysisResult(uuid);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

}
