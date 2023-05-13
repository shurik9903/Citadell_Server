package org.example.model.utils;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.example.data.mydata.DReport;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtils implements IFileUtils{

    @Override
    public boolean writeFile(byte[] content, String filename) throws IOException {

        File file = new File(filename);
        if (!file.exists()) {
            if (! file.createNewFile()) return false;
        }
        //! Файл перезаписывается если он уже существует
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.flush();
        fos.close();

        return true;
    }

    @Override
    public boolean writeFile(Workbook workbook, String filename) throws IOException {

        File file = new File(filename);
        if (!file.exists()) {
            if (! file.createNewFile()) return false;
        }

        FileOutputStream out = new FileOutputStream(filename);
        workbook.write(out);
        workbook.close();
        out.flush();
        out.close();

        return true;
    }

    @Override
    public boolean writeFile(String content, String filename) throws IOException {

        File file = new File(filename);
        if (!file.exists()) {
            if (! file.createNewFile()) return false;
        }

        PrintWriter out = new PrintWriter(new FileWriter(file));

        out.write(content);
        out.flush();
        out.close();

        return true;
    }

    @Override
    public ArrayList<DReport> getReportFile(String docPath) throws Exception {
        try {

            Jsonb jsonb = JsonbBuilder.create();
            FileInputStream jsonFile = new FileInputStream(docPath + ".json");
            String jsonText = new String(jsonFile.readAllBytes());

            DReport[] reports = jsonb.fromJson(jsonText, DReport[].class);

            return new ArrayList<>(Arrays.asList(reports));
        } catch (Exception e){
            throw new Exception("Ошибка при чтении JSON файла");
        }

    }
}
