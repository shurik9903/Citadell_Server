package org.example.controller.API;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.example.controller.request.RequestBuilder;

@Path("/MLPing")
public class MLPing {

        @GET
        @Produces("application/json; charset=UTF-8")
        public Response doGet() {
            try {
                String text = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/Health-Check").send();
                return Response.ok("PING: \n" + text).build();
            } catch (Exception e) {
                System.out.println("|Ошибка: " + e);
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
        }

}
