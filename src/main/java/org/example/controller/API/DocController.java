package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.doc.IDoc;
import org.example.model.token.*;

@Path("/doc")
public class DocController {

    @Inject
    private IDoc doc;

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
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet(@QueryParam("name") String fileName, @QueryParam("start") int start, @QueryParam("diapason") int diapason, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {

            if (fileName == null && start == 0 && diapason == 0){
                return doc.allDocs(userID);
            }

            if (fileName == null || fileName.isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + "Имя файла не может быть пустым").build();
            }

            return doc.readDoc(fileName, start, diapason, userID, userLogin);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doPost(String document, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return doc.saveFile(document, userID, userLogin);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @PUT
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doPut(@QueryParam("name") String fileName, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return doc.overwriteFile(fileName, userID, userLogin);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

}

