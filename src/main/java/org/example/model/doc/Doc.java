package org.example.model.doc;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.data.entity.ENameFiles;
import org.example.model.database.fileWork.IDBFileWork;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.utils.IFileUtils;
import org.example.model.workingFiles.IWorkingFiles;

import java.io.File;

import java.util.*;

public class Doc implements IDoc{

    @Inject
    private IDBFileWork dataBase;

    @Inject
    private IFileUtils fileUtils;

    @Inject
    private IWorkingFiles workingFiles;

    @Override
    public Response loadDoc(String name){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response allDocs(String userID) {

        Jsonb jsonb = JsonbBuilder.create();

        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            ArrayList<ENameFiles> eNameFiles = dataBase.allFiles(userID);

            Result.put("Files", jsonb.toJson(eNameFiles));
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }


    @Override
    public Response updateDoc(String fileName, String docData, String userLogin, String userID) {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();


            IDocReader docReader = DocReaderFactory.getDocReader(fileName.substring(fileName.lastIndexOf('.')));
            docReader.updateDoc(ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName, docData, userID);

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

//    private DExcel excelParser(String fileName, String userLogin){
//
//        try {
//            FileInputStream file = new FileInputStream(ServerProperties.getProperty("filepath") + File.separator + userLogin + File.separator + fileName);
//            Workbook workbook = new XSSFWorkbook(file);
//
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Map<Integer, ArrayList<String>> rows = new HashMap<>();
//            ArrayList<String> title = new ArrayList<>();
//
//            int i = 1;
//            for (Row row : sheet) {
//
//                if (i == 1) {
//                    for (Cell cell : row) {
//                        switch (cell.getCellType()) {
//                            case STRING -> title.add(cell.getStringCellValue());
//                            case NUMERIC -> title.add(String.valueOf(cell.getNumericCellValue()));
//                        }
//                    }
//                } else {
//                    rows.put(i, new ArrayList<>());
//                    for (Cell cell : row) {
//                        switch (cell.getCellType()) {
//                            case STRING -> rows.get(i).add(cell.getStringCellValue());
//                            case NUMERIC -> rows.get(i).add(String.valueOf(cell.getNumericCellValue()));
//                        }
//                    }
//                }
//                i++;
//            }
//
//            file.close();
//
//            return new DExcel(sheet.getPhysicalNumberOfRows(), rows, title, null);
//
//        }catch (Exception e){
//            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
//            return new DExcel("Error: " + e.getMessage());
//        }
//    }

    @Override
    public Response readDoc(String docName, int start, int diapason, String userid, String userLogin) {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            File customDir = new File(ServerProperties.getProperty("filepath") + File.separator + userLogin);
            String doc_path = customDir.getCanonicalPath() + File.separator + docName;

            if (!customDir.exists()){
                customDir.mkdir();
            }

            if(!(new File(doc_path).exists())){
                workingFiles.loadFile(userid, userLogin, docName);
            }

            IDocReader docReader = DocReaderFactory.getDocReader(docName.substring(docName.lastIndexOf('.')));
            Result.put("Data", docReader.parser(doc_path, start, diapason));

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


}
