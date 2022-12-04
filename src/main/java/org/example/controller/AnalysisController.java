package org.example.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.analysis.IAnalysis;
import org.example.model.token.ITokenKey;
import org.example.model.token.ITokenValidator;
import org.example.model.token.TokenKey;
import org.example.model.token.TokenValidator;

@Path("/analysis")
public class AnalysisController {

    @Inject
    private IAnalysis analysis;

    @GET
    @Path("/ping")
    public String ping() {
        return "Ping";
    }

    @GET
    @Path("{docid:.*}")
    @Produces("application/json")
    public Response doGet(@PathParam("docid") String docid, @HeaderParam("Token") String UserToken) {
        try {
            try {
                ITokenKey tokenKey = new TokenKey();
                ITokenValidator tokenValidator = new TokenValidator(tokenKey.getKey());
                tokenValidator.validate(UserToken);
            } catch (Exception e) {
                return Response.status(Response.Status.FORBIDDEN).entity("|Error: " + e.getMessage()).build();
            }

            return analysis.loadAnalysis(docid);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

}
