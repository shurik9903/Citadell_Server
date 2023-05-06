package org.example.data.mydata;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DExcel {

    private Integer rowNumber = 0;
    private Map<Integer, ArrayList<String>> rows = null;
    private ArrayList<String> title = null;
    private String Msg = "";

    public DExcel(){}

    public DExcel(String Msg){
        this.Msg = Msg;
    }

    public DExcel(Integer rowNumber, Map<Integer, ArrayList<String>> rows, ArrayList<String> title){
        this.rowNumber = rowNumber;
        this.rows = rows;
        this.title = title;
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Map<Integer, ArrayList<String>> getRows() {
        return rows;
    }

    public void setRows(Map<Integer, ArrayList<String>> rows) {
        this.rows = rows;
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
        json.put("Rows", jsonb.toJson(rows));
        json.put("Title", jsonb.toJson(title));

        return json;
    }

}
