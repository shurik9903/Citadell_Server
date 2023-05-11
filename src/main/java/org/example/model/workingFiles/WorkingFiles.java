package org.example.model.workingFiles;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.database.IDataBaseWork;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorkingFiles implements IWorkingFiles {

    @Inject
    private IDataBaseWork DataBaseWork;

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

            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + userLogin  + File.separator + docReader.getFullName());
            FileInputStream input = new FileInputStream(fileDownload);

            MutableBoolean replace = new MutableBoolean(false);
            Result.put("Msg", DataBaseWork.saveFile(docReader.getFullName(), input.readAllBytes(), userID, replace));
            Result.put("Replace", replace.toString());

            input.close();

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response overwriteFile(String doc_name, String userid, String userLogin){

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + doc_name);
            FileInputStream input = new FileInputStream(fileDownload);

            Result.put("Msg", DataBaseWork.overwriteFile(doc_name, input.readAllBytes(), userid));

            input.close();

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response loadFile(){

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
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
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }
}
