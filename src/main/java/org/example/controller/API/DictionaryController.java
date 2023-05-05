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
    @Path("{word:.*}")
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet(@PathParam("word") String word) {
        try {
            return dictionary.loadWord(word);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }
}
