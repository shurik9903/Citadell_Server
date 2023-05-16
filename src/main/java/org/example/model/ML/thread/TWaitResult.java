package org.example.model.ML.thread;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DUserConnect;

import java.util.HashMap;
import java.util.Map;

public class TWaitResult implements Runnable {

    private Thread thread;
    private final String uuid;
    private final String filename;
    private final String user;
    private String message;

    public TWaitResult(String threadName, String uuid, String fileName, String user) {
        thread = new Thread(this, threadName);

        this.uuid = uuid;
        this.filename = fileName;
        this.user = user;

        thread.start();
    }

    @Override
    public void run() {
        try {
            while (call()) {
                Thread.sleep(60000);
            }
            if (!message.isEmpty()){
                System.out.println(message);
            }
        } catch (InterruptedException e) {
            System.out.println("|Ошибка потока " + thread.getName() + ": " + e.getMessage());
        }
    }

    public boolean call(){

        String text;

        try {
            if (uuid == null) {
                text = "|Ошибка потока " + thread.getName() + ": Не указан uuid анализируемых данных";
                System.out.println(text);
                message = text;
                return false;
            }

            RequestBuilder request = new RequestBuilder(RequestBuilder.Method.POST, "api/v1/predict/result/" + uuid);

            String result = request.send();

            if (request.responseCode != 200) {
                text = "|Ошибка потока " + thread.getName() + ": Ошибка при обращении к серверу ML" + request.responseCode + "\n" + result;
                System.out.println(text);
                message = text;
                return false;
            }

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> data = new HashMap<>();
            data = (Map<String, String>) jsonb.fromJson(result, data.getClass());

            String info = data.getOrDefault("message", "");

            if (info.isEmpty()){
                text = "|Ошибка потока " + thread.getName() + ": Ошибка при обработке данных сервера ML" + request.responseCode + "\n" + result;
                System.out.println(text);
                message = text;
                return false;
            }

            System.out.println("info " + info);

            if (info.equals("true")) {
                OutMessage outMessage = new OutMessage();
                outMessage.setLogin(user);
                outMessage.setType("FileResult");
                outMessage.setMessage(jsonb.toJson(new HashMap<>(){{
                    put("uuid", uuid);
                    put("fileName", filename);
                }}));

                DUserConnect.Analysis analysis = new DUserConnect.Analysis();

                analysis.setUuid(uuid);
                analysis.setFileName(filename);
                analysis.setStatus(false);

                new WebSocket().sendResultMessage(outMessage, user, analysis);
            }

            return true;
        } catch (Exception e) {
            text = "|Ошибка потока " + thread.getName() + ": " + e.getMessage();
            System.out.println(text);
            message = text;
            return false;
        }
    }
}
