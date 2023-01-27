package org.example.data.mydata;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DExcel {

    private Integer rowNumber = 0;
    private Map<Integer, ArrayList<String>> data = null;
    private String Msg = "";

    public DExcel(){}

    public DExcel(String Msg){
        this.Msg = Msg;
    }

    public DExcel(Integer rowNumber, Map<Integer, ArrayList<String>> data){
        this.rowNumber = rowNumber;
        this.data = data;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Map<Integer, ArrayList<String>> getData() {
        return data;
    }

    public void setData(Map<Integer, ArrayList<String>> data) {
        this.data = data;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public Map<String, String> toJson(){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> json = new HashMap<>();

        json.put("RowNumber", rowNumber.toString());
        json.put("Rows", jsonb.toJson(data));

        return json;
    }

}
