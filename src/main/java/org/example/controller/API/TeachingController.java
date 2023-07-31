package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.analysis.IAnalysis;
import org.example.model.teaching.ITeaching;
import org.example.model.token.TokenRequired;

@Path("/teaching")
public class TeachingController {

    @Inject
    private ITeaching teaching;

    @POST
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doPost(String document, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return teaching.startTeaching(document, userLogin, userID);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

//    @GET
//    @Path("/status/{uuid}")
//    @Produces("application/json; charset=UTF-8")
//    @TokenRequired
//    public Response doGetStatus(@PathParam("uuid") String uuid) {
//        try {
//            return analysis.getAnalysisStatus(uuid);
//        } catch (Exception e) {
//            System.out.println("|Ошибка: " + e);
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("/result/{uuid}")
//    @Produces("application/json; charset=UTF-8")
//    @TokenRequired
//    public Response doGetResult(@PathParam("uuid") String uuid, @HeaderParam("login") String userLogin) {
//        try {
//            return analysis.getAnalysisResult(uuid, userLogin);
//        } catch (Exception e) {
//            System.out.println("|Ошибка: " + e);
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
//        }
//    }

}