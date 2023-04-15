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
import org.example.data.entity.EFile;
import org.example.data.mydata.DExcel;
import org.example.model.database.IDataBaseWork;
import org.example.model.properties.ServerProperties;
import org.example.model.workingFiles.IWorkingFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
                Result.put("Msg", "No connection to server.");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            Result.put("Msg", "");
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

    @Override
    public Response saveFile(String document, String userID){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        Map<String, String> docData = new HashMap<>();
        docData = (Map<String,String>) jsonb.fromJson(document, docData.getClass());

        String doc_name  = docData.getOrDefault("doc_name", "");
        String doc_bytes  = docData.getOrDefault("doc_bytes", "");

        if (! (doc_name.endsWith(".xlsx")
                || doc_name.endsWith(".xlsm")
                || doc_name.endsWith(".xls"))
        ) {
            Result.put("Msg", "|Error: Unsupported file format. \nSupported formats .xlsx, .xlsm, .xlsm.");
            return Response.ok(jsonb.toJson(Result)).build();
        }

        try {
            ArrayList<String> strBytes = new ArrayList<>(
                    Arrays.asList(doc_bytes
                            .replace("[","")
                            .replace("]","")
                            .replace(" ", "")
                            .split(","))
            );

            ArrayList<Byte> bytes = new ArrayList<>();

            for (String e : strBytes) {
                bytes.add((byte) Integer.parseInt(e));
            }

            File customDir = new File(ServerProperties.getProperty("filepath"));
            if (!customDir.exists()) {
                customDir.mkdir();
            }

            String doc_path = customDir.getCanonicalPath() + File.separator + doc_name;
            if (! workingFiles.writeFile(ArrayUtils.toPrimitive(bytes.toArray(new Byte[0])), doc_path)) {
                Result.put("Msg", "File save error.");
                return Response.ok(jsonb.toJson(Result)).build();
            }

        } catch (Exception e){
            System.out.println("File save error: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }

//        try {
//            File fileDownload = new File("..//test" + File.separator + "0e6b95c2b8eee44ccbd042dbefadfed2.jpg");
//
//            ArrayList<Integer> loadInt = new ArrayList<>();
//
//            for (Byte b: Files.readAllBytes(fileDownload.toPath())){
//                loadInt.add(Byte.toUnsignedInt(b));
//            }
//
//            Result.put("fileName", fileDownload.getName());
//            Result.put("fileBytes", loadInt.toString());
//
//        } catch (Exception e){
//            System.out.println("Error file load: " + e.getMessage());
//            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
//        }


        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server.");
                return Response.ok(jsonb.toJson(Result)).build();
            }

//            ArrayList<String> strBytes = new ArrayList<>(
//                    Arrays.asList(doc_bytes
//                            .replace("[","")
//                            .replace("]","")
//                            .replace(" ", "")
//                            .split(","))
//            );
//
//            ArrayList<Byte> bytes = new ArrayList<>();
//
//            for (String e : strBytes) {
//                bytes.add((byte) Integer.parseInt(e));
//            }

//            Result.put("Msg", DataBaseWork.saveFile(doc_name, ArrayUtils.toPrimitive(bytes.toArray(new Byte[0])), userid));

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + doc_name);
            FileInputStream input = new FileInputStream(fileDownload);

            MutableBoolean replace = new MutableBoolean(false);
            Result.put("Msg", DataBaseWork.saveFile(doc_name, input.readAllBytes(), userID, replace));
            Result.put("Replace", replace.toString());
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }

    @Override
    public Response overwriteFile(String doc_name, String userid){

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server.");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            File fileDownload = new File(ServerProperties.getProperty("filepath") + File.separator + doc_name);
            FileInputStream input = new FileInputStream(fileDownload);

            Result.put("Msg", DataBaseWork.overwriteFile(doc_name, input.readAllBytes(), userid));
            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
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

            Result.put("fileName", fileDownload.getName());
            Result.put("fileBytes", loadInt.toString());

        } catch (Exception e){
            System.out.println("File upload error: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server.");
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

            Map<Integer, ArrayList<String>> data = new HashMap<>();

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

                data.put(i, new ArrayList<>());
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING -> data.get(i).add(cell.getStringCellValue());
                        case NUMERIC -> data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                    }
                }
            }

            file.close();

            return new DExcel(sheet.getPhysicalNumberOfRows(), data);

        }catch (Exception e){
            System.out.println("Excel Parser Error: " + e.getMessage());
            return new DExcel("Error: " + e.getMessage());
        }
    }

    private DExcel excelParser(String fileName){

        try {
            FileInputStream file = new FileInputStream(ServerProperties.getProperty("filepath") + File.separator + fileName);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, ArrayList<String>> data = new HashMap<>();

            int i = 1;
            for (Row row : sheet) {
                data.put(i, new ArrayList<>());
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING -> data.get(i).add(cell.getStringCellValue());
                        case NUMERIC -> data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                    }
                }
                i++;
            }

            file.close();

            return new DExcel(sheet.getPhysicalNumberOfRows(), data);

        }catch (Exception e){
            System.out.println("Excel Parser Error: " + e.getMessage());
            return new DExcel("Error: " + e.getMessage());
        }
    }

    @Override
    public Response readDoc(String doc_name, int start, int diapason, String userid) {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Not connection to server.");
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
                Result.put("Msg", "File save error.");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            Result.put("Data", jsonb.toJson(excelParser(eFile.getFile_name(), start, diapason).toJson()));
            Result.put("Msg", "");

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
    }


}
