package org.example.model.utils;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;
import org.apache.poi.ss.usermodel.Workbook;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.data.mydata.DReport;
import org.example.data.mydata.DUserConnect;
import org.example.model.connections.IUserConnections;
import org.example.model.properties.ServerProperties;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

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

            File file = new File(docPath + ".json");
            if (!file.exists()) {
                if (! file.createNewFile()) throw new Exception("Ошибка при чтении Report файла");

                PrintWriter out = new PrintWriter(new FileWriter(file));

                out.write("[]");
                out.flush();
                out.close();
            }

            FileInputStream jsonFile = new FileInputStream(docPath + ".json");
            String jsonText = new String(jsonFile.readAllBytes());

            jsonFile.close();

            DReport[] reports = jsonb.fromJson(jsonText, DReport[].class);

            return new ArrayList<>(Arrays.asList(reports));
        } catch (Exception e){
            throw new Exception("Ошибка при чтении JSON файла");
        }
    }

    @Override
    public void logs(String text) throws Exception {
        try {
            String logs = ServerProperties.getProperty("fileLogPath");

            File file = new File(logs+"/ServerLog.txt");
            if (!file.exists()) {
                if (!file.createNewFile()) throw new Exception("Ошибка при работе с log файлом: " + logs);
            }

            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String date = formatter.format(new Date());

            out.write("["+date+"]: " + text);
            out.close();
        } catch (Exception e){
            throw new Exception("Ошибка при работе с logs файлом");
        }
    }

    @Override
    public void deleteFile(String docPath) throws Exception {
        try {

            File file = new File(docPath);
            File fileJson = new File(docPath + ".json");

            if (file.exists()) {
                file.delete();
            }

            if(fileJson.exists()){
                fileJson.delete();
            }

        } catch (Exception e){
            throw new Exception("Ошибка при удалении файла пользователя");
        }
    }

}
