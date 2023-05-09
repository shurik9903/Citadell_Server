package org.example.model.doc;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.data.entity.ENameFiles;
import org.example.data.entity.EFile;
import org.example.data.mydata.DExcel;
import org.example.model.database.IDataBaseWork;
import org.example.model.doc.docReader.DocReaderFactory;
import org.example.model.doc.docReader.IDocReader;
import org.example.model.properties.ServerProperties;
import org.example.model.workingFiles.IWorkingFiles;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.*;

public class Doc implements IDoc{

    @Inject
    private IDataBaseWork DataBaseWork;

    @Inject
    private IWorkingFiles workingFiles;

    @Override
    public Response loadDoc(String name){
        Jsonb jsonb = JsonbBuilder.create();

        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }



            Result.put("Msg", "");
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response allDocs(String userID) {

        Jsonb jsonb = JsonbBuilder.create();



        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            StringBuilder msg = new StringBuilder();
            ArrayList<ENameFiles> eNameFiles = DataBaseWork.allFiles(userID, msg);

            if (!msg.isEmpty()){
                Result.put("Msg", "");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            Result.put("Files", jsonb.toJson(eNameFiles));
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }

    }

    @Override
    public Response saveFile(String document, String userID){
        try {

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        Map<String, String> data = new HashMap<>();
        data = (Map<String, String>) jsonb.fromJson(document, data.getClass());

        String type = data.getOrDefault("type", null);

        IDocReader docReader = DocReaderFactory.getDocReader(type);

        docReader.setDoc(document);

            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + docReader.getFullName());
            FileInputStream input = new FileInputStream(fileDownload);

            MutableBoolean replace = new MutableBoolean(false);
            Result.put("Msg", DataBaseWork.saveFile(docReader.getFullName(), input.readAllBytes(), userID, replace));
            Result.put("Replace", replace.toString());
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }

    @Override
    public Response overwriteFile(String doc_name, String userid){

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + doc_name);
            FileInputStream input = new FileInputStream(fileDownload);

            Result.put("Msg", DataBaseWork.overwriteFile(doc_name, input.readAllBytes(), userid));
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
            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + "0e6b95c2b8eee44ccbd042dbefadfed2.jpg");

            ArrayList<Integer> loadInt = new ArrayList<>();

            for (Byte b: Files.readAllBytes(fileDownload.toPath())){
                loadInt.add(Byte.toUnsignedInt(b));
            }

            Result.put("file_name", fileDownload.getName());
            Result.put("file_byte", loadInt.toString());

        } catch (Exception e){
            System.out.println("Ошибка при загрузке файла: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            Result.put("Msg", "");
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

    private DExcel excelParser(String fileName, int start, int number){

        if (start < 1)
            start = 1;
//            return new DExcel("Start cannot be less than 1");


        if (number < 1)
            number = 25;
//            return new DExcel("Quantity cannot be less than 1");

        try {
            FileInputStream file = new FileInputStream(ServerProperties.getProperty("filepath") + File.separator + fileName);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, ArrayList<String>> rows = new HashMap<>();
            ArrayList<String> title = new ArrayList<>();

//            for (int i = start; i <= sheet.getPhysicalNumberOfRows() && i <= start + number; i++){
//                sheet.getRow(i);
//
//            }

            int i = 0;
            for (Row row : sheet) {
                i++;
                if (i < start)
                    continue;

                if (i > start + number - 1) break;

                if (i == 1) {
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING -> title.add(cell.getStringCellValue());
                            case NUMERIC -> title.add(String.valueOf(cell.getNumericCellValue()));
                        }
                    }
                } else {
                    rows.put(i, new ArrayList<>());
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING -> rows.get(i).add(cell.getStringCellValue());
                            case NUMERIC -> rows.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        }
                    }
                }
            }

            file.close();

            return new DExcel(sheet.getPhysicalNumberOfRows(), rows, title);

        }catch (Exception e){
            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
            return new DExcel("Error: " + e.getMessage());
        }
    }

    private DExcel excelParser(String fileName){

        try {
            FileInputStream file = new FileInputStream(ServerProperties.getProperty("filepath") + File.separator + fileName);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, ArrayList<String>> rows = new HashMap<>();
            ArrayList<String> title = new ArrayList<>();

            int i = 1;
            for (Row row : sheet) {

                if (i == 1) {
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING -> title.add(cell.getStringCellValue());
                            case NUMERIC -> title.add(String.valueOf(cell.getNumericCellValue()));
                        }
                    }
                } else {
                    rows.put(i, new ArrayList<>());
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING -> rows.get(i).add(cell.getStringCellValue());
                            case NUMERIC -> rows.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        }
                    }
                }
                i++;
            }

            file.close();

            return new DExcel(sheet.getPhysicalNumberOfRows(), rows, title);

        }catch (Exception e){
            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
            return new DExcel("Error: " + e.getMessage());
        }
    }

    @Override
    public Response readDoc(String doc_name, int start, int diapason, String userid) {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Нет соединения с базой данных");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            EFile eFile = DataBaseWork.loadFile(doc_name, userid);
            if (eFile.getMsg() != null) {
                Result.put("Msg", eFile.getMsg());
                return Response.ok(jsonb.toJson(Result)).build();
            }


            File customDir = new File(ServerProperties.getProperty("filepath"));
            if (!customDir.exists()) {
                customDir.mkdir();
            }
            String doc_path = customDir.getCanonicalPath() + File.separator + eFile.getFile_name();

            if (! workingFiles.writeFile(eFile.getFile_byte(), doc_path)) {
                Result.put("Msg", "Ошибка при сохранении файла");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            Result.put("Data", jsonb.toJson(excelParser(eFile.getFile_name(), start, diapason).toJson()));
            Result.put("Msg", "");

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Ошибка: " + e.getMessage()).build();
        }
    }


}
