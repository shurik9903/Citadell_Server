package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.analysis.IAnalysis;
import org.example.model.token.*;

@Path("/analysis")
public class AnalysisController {

    @Inject
    private IAnalysis analysis;

    @GET
    @Path("{docid:.*}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet(@PathParam("docid") String docid) {
        try {
            return analysis.loadAnalysis(docid);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

}
