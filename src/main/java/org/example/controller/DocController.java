package org.example.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.doc.IDoc;
import org.example.model.token.ITokenKey;
import org.example.model.token.ITokenValidator;
import org.example.model.token.TokenKey;
import org.example.model.token.TokenValidator;

@Path("/doc")
public class DocController {

    @Inject
    private IDoc doc;

    @GET
    @Path("/ping")
    public String ping() {
        return "Ping";
    }

    @GET
    @Path("{name:.*}")
    @Produces("application/json")
    public Response doGet(@PathParam("name") String name, @HeaderParam("Token") String UserToken) {
        try {
            try {
                ITokenKey tokenKey = new TokenKey();
                ITokenValidator tokenValidator = new TokenValidator(tokenKey.getKey());
                tokenValidator.validate(UserToken);
            } catch (Exception e) {
                return Response.status(Response.Status.FORBIDDEN).entity("|Error: " + e.getMessage()).build();
            }

            return doc.loadDoc(name);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

}

