package org.example.controller.API;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.model.doc.IDoc;
import org.example.model.report.IReport;
import org.example.model.token.TokenRequired;

@Path("/report")
public class ReportController {

    @Inject
    private IReport report;

    @GET
    @Produces("application/json; charset=UTF-8")
    @TokenRequired
    public Response doGet(@QueryParam("name") String fileName, @QueryParam("row") String row, @HeaderParam("login") String userLogin) {
        try {

            if ((fileName == null || fileName.isEmpty()) && (row == null || row.isEmpty())){
                return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + "Имя файла/строки не может быть пустым").build();
            }

            return report.getReport(fileName, row, userLogin);
        } catch (Exception e) {
            System.out.println("|Error: " + e);
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

}

