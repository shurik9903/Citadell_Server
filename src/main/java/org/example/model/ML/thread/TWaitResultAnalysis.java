package org.example.model.ML.thread;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.controller.WebSocket.WebSocket;
import org.example.controller.request.RequestBuilder;
import org.example.data.mydata.DUserConnect;
import org.example.model.utils.FileUtils;
import org.example.model.utils.IFileUtils;


import java.util.HashMap;
import java.util.Map;

public class TWaitResultAnalysis implements Runnable {

    public Thread getThread() {
        return thread;
    }

    private final Thread thread;
    private final String uuid;
    private final String type;

    private String message = "";
    private final IFileUtils fileUtils = new FileUtils();

    public TWaitResultAnalysis(String threadName, String uuid, String type) {
        this.uuid = uuid;
        this.type = type;
        thread = new Thread(this, threadName);
    }

    @Override
    public void run() {
        try {
            while (call()) {
                Thread.sleep(5000);
            }

            if (!message.isEmpty()){
                try {
                    fileUtils.logs(message);
                } catch (Exception e){
                    System.out.println(message);
                }
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

            RequestBuilder request;

            switch (type){
                case "predict" ->
                    request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/predict/status/" + uuid);

                case "teach" ->
                    request = new RequestBuilder(RequestBuilder.Method.GET, "api/v1/teach/status/" + uuid);

                default -> {
                    text = "Данного потокового типа не найдено: " + type;
                    System.out.println(text);
                    message = text;
                    return false;
                }
            }


            String result = request.send();

            System.out.println("Wait Result: " + result);

            if (request.responseCode != 200) {
                text = "|Ошибка потока " + thread.getName() + ": Ошибка при обращении к серверу ML " + request.responseCode + "\n" + result;
                System.out.println(text);
                message = text;
                return false;
            }

            Jsonb jsonb = JsonbBuilder.create();
            Map<String, String> data = new HashMap<>();
            data = (Map<String, String>) jsonb.fromJson(result, data.getClass());

            String info = data.getOrDefault("Message", "");

            if (info.isEmpty()){
                text = "|Ошибка потока " + thread.getName() + ": Ошибка при обработке данных сервера ML " + request.responseCode + "\n" + result;
                System.out.println(text);
                message = text;
                return false;
            }
            return !info.equals("Finish");
        } catch (Exception e) {
            text = "|Ошибка потока " + thread.getName() + ": " + e.getMessage();
            System.out.println(text);
            message = text;
            return false;
        }
    }
}
