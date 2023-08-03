package org.example.model.report;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.data.mydata.DReport;
import org.example.model.database.reportWork.IDBReportWork;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report implements IReport{

    @Inject
    private IDBReportWork dataBase;

    @Inject
    private IFileUtils fileUtils;

    @Override
    public Response getReport(String fileName, String row, String userLogin){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            String path = ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName;

            if (new File(path).exists()) {

                ArrayList<DReport> reports = fileUtils.getReportFile(path);

                DReport find =
                        reports.stream().filter((e)->
                     e.getRowNum().equals(row)
                ).toList().get(0);

                if (find == null){
                    throw new Exception("Данный отчет отсутствует");
                }
                Result.put("report", find.getMessage());
            }

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


}
