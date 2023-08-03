package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import org.example.model.dictionary.IDictionary;
import org.example.model.token.*;

@Path("/dictionary")
public class DictionaryController {

    @Inject
    private IDictionary dictionary;

    @GET
    @Path("/simple/{word}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetSimpleWord(@PathParam("word") String word) {
        try {
            return dictionary.loadSimpleWord(word);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/simple")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetSimpleWords() {
        try {
            return dictionary.loadSimpleWords();
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/simple")
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doPostSimpleWord(String word) {
        try {
            return dictionary.saveSimpleWord(word);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/simple")
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doPutSimpleWord(String word) {
        try {
            return dictionary.updateSimpleWord(word);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/simple/{wordID}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doDeleteSimpleWord(@PathParam("wordID") int wordID) {
        try {
            return dictionary.deleteSimpleWord(wordID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/spelling/{word}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetSpellingWord(@PathParam("word") String word) {
        try {
            return dictionary.loadSpellingWord(word);
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/spelling")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetSpellingWords() {
        try {
            return dictionary.loadSpellingWords();
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/spelling")
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doPostSpellingWord(String word) {
        try {
            return dictionary.saveSpellingWord(word);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/spelling")
    @Consumes("application/json")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doPutSpellingWord(String word) {
        try {
            return dictionary.updateSpellingWord(word);
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/spelling/{wordID}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired(admin = true)
    public Response doDeleteSpellingWord(@PathParam("wordID") int wordID) {
        try {
            return dictionary.deleteSpellingWord(wordID);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/type")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGetTypeWords() {
        try {
            return dictionary.loadTypeWords();
        } catch (Exception e) {
            System.out.println("|Ошибка: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
