package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.token.TokenRequired;
import org.example.model.workingFiles.IWorkingFiles;

@Path("/file")
public class FileController {

    @Inject
    private IWorkingFiles file;

    @POST
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doPost(String document, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return file.saveFile(document, userID, userLogin);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @PUT
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doPut(@QueryParam("name") String fileName, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return file.overwriteFile(fileName, userID, userLogin);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet(@QueryParam("name") String fileName, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return file.loadFile(userID, userLogin, fileName);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doDelete(@QueryParam("name") String fileName, @HeaderParam("X-Authentication-decrypted") String userID, @HeaderParam("login") String userLogin) {
        try {
            return file.deleteFile(fileName, userLogin, userID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}