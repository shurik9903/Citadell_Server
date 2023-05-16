package org.example.controller.request;

import jakarta.json.bind.JsonbBuilder;
import okhttp3.*;
import org.example.model.properties.ServerProperties;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    private final String IP = ServerProperties.getProperty("ML_IP");
    private final String PORT = ServerProperties.getProperty("ML_PORT");

    private final OkHttpClient httpClient = new OkHttpClient();

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    private final Method method;
    private final String address;
    private Map<Object, Object> headers = new HashMap<>();
    private Map<Object, Object> parameters = new HashMap<>();
    private Map<Object, Object> body = new HashMap<>();

    public int responseCode = 0;

    public RequestBuilder(Method method, String address){
        this.method = method;
        this.address = address;
    }

    public RequestBuilder setHeader(Map<Object, Object> headers){
        this.headers = headers;
        return this;
    }

    public RequestBuilder setParameters(Map<Object, Object> parameters){
        this.parameters = parameters;
        return this;
    }

    public RequestBuilder setBody(Map<Object, Object> body){
        this.body = body;
        return this;
    }

    private static String getParamsString(Map<Object, Object> params) {
        StringBuilder result = new StringBuilder();
        result.append("?");

        for (Map.Entry<Object, Object> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 1
                ? resultString.substring(0, resultString.length() - 1)
                : "";
    }


    public String send() throws Exception{

        Request.Builder builder = new Request.Builder();

        String paramsString = "";

        if (method == Method.GET)
            paramsString = getParamsString(parameters);

        builder.url("http://" + IP + ":" + PORT + "/" + address + paramsString);

        for (Map.Entry<Object, Object> head : headers.entrySet()){
            builder.addHeader(head.getKey().toString(), head.getValue().toString());
        }
        
        RequestBody requestBody = RequestBody.create("", MediaType.get("application/json; charset=utf-8"));

        if (!body.isEmpty()) {
            String json = JsonbBuilder.create().toJson(body);
            requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        }

        switch (method) {
            case POST -> builder.post(requestBody);

            case PUT -> builder.put(requestBody);

            case DELETE -> builder.delete(requestBody);
        }

        Request request = builder.build();

        try (Response response = httpClient.newCall(request).execute()) {

            responseCode = response.code();

            if (response.code() == 200) {
                if (response.body() != null)
                    return response.body().string();
                else
                    return "";
            }
            return "";
        }
    }


//
//    protected HttpURLConnection connection(String address) throws Exception {
//        URL obj = new URL("http://" + IP + ":" + PORT + "/" + address);
//        return  (HttpURLConnection) obj.openConnection();
//    }
//
//    public String send(Method method, String address, Map<String, String> headers, Map<String, String> parameters) throws Exception {
//
//        HttpURLConnection con = connection(address);
//
//        con.setRequestMethod(method.toString());
//
//        for (Map.Entry<String, String> entry : headers.entrySet()){
//            con.addRequestProperty(entry.getKey(), entry.getValue());
//        }
//
//        if(!parameters.isEmpty() && (method != Method.GET)){
//            con.setDoOutput(true);
//            DataOutputStream out = new DataOutputStream(con.getOutputStream());
//            out.writeBytes(getParamsString(parameters));
//            out.flush();
//            out.close();
//        }
//
//        int responseCode = con.getResponseCode();
//
//        if (responseCode == HttpURLConnection.HTTP_OK) { // success
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            return response.toString();
//        } else {
//            return null;
//        }
//
//    }
//

}
