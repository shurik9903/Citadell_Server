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

//    @GET
////    @Path("{name:.*}")
//    @Produces("application/json")
//    public Response doGet(@QueryParam("name") String fileName, @HeaderParam("UserID") String userID ,@HeaderParam("Token") String UserToken) {
//        try {
//            try {
//                ITokenKey tokenKey = new TokenKey();
//                ITokenValidator tokenValidator = new TokenValidator(tokenKey.getKey());
//                tokenValidator.validate(UserToken);
//            } catch (Exception e) {
//                return Response.status(Response.Status.FORBIDDEN).entity("|Error: " + e.getMessage()).build();
//            }
//            System.out.println("Test1: " + fileName);
//            return doc.readDoc(fileName, userID);
//        } catch (Exception e) {
//            System.out.println("|Error: " + e);
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
//        }
//    }

    @GET
//    @Path("{name:.*}")
    @Produces("application/json")
    public Response doGet(@QueryParam("name") String fileName, @QueryParam("start") int start, @QueryParam("diapason") int diapason, @HeaderParam("UserID") String userID ,@HeaderParam("Token") String UserToken) {
        try {
            try {
                ITokenKey tokenKey = new TokenKey();
                ITokenValidator tokenValidator = new TokenValidator(tokenKey.getKey());
                tokenValidator.validate(UserToken);
            } catch (Exception e) {
                return Response.status(Response.Status.FORBIDDEN).entity("|Error: " + e.getMessage()).build();
            }

            if (fileName == null || fileName.isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + "Filename cannot be empty").build();
            }

            return doc.readDoc(fileName, start, diapason, userID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response doPost(String document,  @HeaderParam("Token") String UserToken) {
        try {
            try {
                ITokenKey tokenKey = new TokenKey();
                ITokenValidator tokenValidator = new TokenValidator(tokenKey.getKey());
                tokenValidator.validate(UserToken);
            } catch (Exception e) {
                return Response.status(Response.Status.FORBIDDEN).entity("|Error: " + e.getMessage()).build();
            }

            return doc.saveFile(document);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
//    @Path("{name:.*}")
    @Produces("application/json")
    public Response doPut(@QueryParam("name") String fileName, @HeaderParam("UserID") String userID ,@HeaderParam("Token") String UserToken) {
        try {
            try {
                ITokenKey tokenKey = new TokenKey();
                ITokenValidator tokenValidator = new TokenValidator(tokenKey.getKey());
                tokenValidator.validate(UserToken);
            } catch (Exception e) {
                return Response.status(Response.Status.FORBIDDEN).entity("|Error: " + e.getMessage()).build();
            }

            return doc.overwriteFile(fileName, userID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

}

