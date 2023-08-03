package org.example.model.workingFiles;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.data.entity.EFile;
import org.example.data.entity.EReport;
import org.example.data.mydata.DReport;
import org.example.model.database.fileWork.IDBFileWork;
import org.example.model.database.reportWork.IDBReportWork;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.IFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkingFiles implements IWorkingFiles {

    @Inject
    private IDBFileWork dataBaseFileWork;

    @Inject
    private IDBReportWork dataBaseReportWork;

    @Inject
    private IFileUtils fileUtils;

    @Override
    public Response saveFile(String document, String userID, String userLogin){
        try {

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> Result = new HashMap<>();

            Map<String, String> data = new HashMap<>();
            data = (Map<String, String>) jsonb.fromJson(document, data.getClass());

            String type = data.getOrDefault("type", null);

            IDocReader docReader = DocReaderFactory.getDocReader(type);

            docReader.setDoc(document);
            docReader.saveFile(ServerProperties.getProperty("filepath") + File.separator + userLogin);

            dataBaseFileWork.ping();

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + userLogin  + File.separator + docReader.getFullName());
            FileInputStream input = new FileInputStream(fileDownload);

            MutableBoolean replace = new MutableBoolean(false);
            dataBaseFileWork.saveFile(docReader.getFullName(), input.readAllBytes(), userID, replace);
            Result.put("Replace", replace.toString());

            input.close();

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response overwriteFile(String docName, String userID, String userLogin){

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBaseFileWork.ping();

            String docPath = ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + docName;

            File fileDownload = new File(docPath);
            FileInputStream input = new FileInputStream(fileDownload);

            dataBaseFileWork.overwriteFile(docName, input.readAllBytes(), userID);



            ArrayList<DReport> reports = fileUtils.getReportFile(docPath);

            if (!reports.isEmpty())
                dataBaseReportWork.saveReports(docName, userID, reports);

            input.close();

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response loadFile(String userID, String userLogin, String docName){

//        Jsonb jsonb = JsonbBuilder.create();
//        Map<String, String> Result = new HashMap<>();
//
//        try {


//            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName);
//
//            ArrayList<Integer> loadInt = new ArrayList<>();
//
//            for (Byte b: Files.readAllBytes(fileDownload.toPath())){
//                loadInt.add(Byte.toUnsignedInt(b));
//            }
//
//            Result.put("file_name", fileDownload.getName());
//            Result.put("file_byte", loadInt.toString());
//
//        } catch (Exception e){
//            System.out.println("Ошибка при загрузке файла: " + e.getMessage());
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
//        }
//
//        try {
//            if (!DataBaseWork.ping()) {
//                Result.put("Msg", "Нет соединения с базой данных");
//                return Response.ok(jsonb.toJson(Result)).build();
//            }
//
//            Result.put("Msg", "");





//            return Response.ok(jsonb.toJson(Result)).build();
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
//        }

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBaseFileWork.ping();

            EFile eFile = dataBaseFileWork.loadFile(docName, userID);

            String doc_path = ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + eFile.getFile_name();

            if (! fileUtils.writeFile(eFile.getFile_byte(), doc_path)) {
                throw new Exception("Ошибка при сохранении файла");
            }

            ArrayList<EReport> eReports = dataBaseReportWork.loadReports(docName, userID);

            String writeText = "[]";

            if (eReports != null && !eReports.isEmpty()){
                ArrayList<DReport> dReports = eReports.stream().map(e->{
                    DReport dReport = new DReport();
                    dReport.setRowNum(String.valueOf(e.getRow_num()));
                    dReport.setUserID(String.valueOf(e.getUserID()));
                    dReport.setMessage(e.getMessage());
                    dReport.setType(0);
                    return dReport;
                }).collect(Collectors.toCollection(ArrayList::new));

                writeText = jsonb.toJson(dReports);
            }

            if (! fileUtils.writeFile(writeText, doc_path + ".json")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Ошибка при сохранении файла").build();
            }

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteFile(String fileName, String userLogin, String userID) {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBaseFileWork.ping();

            String docPath = ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName;

            File fileDownload = new File(docPath);
            FileInputStream input = new FileInputStream(fileDownload);

            dataBaseFileWork.deleteFile(fileName, userID);

            fileUtils.deleteFile(docPath);

            input.close();

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


}
