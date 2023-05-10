package org.example.data.mydata;

import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.w3c.dom.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DDocXLS {

     public static class SizeTable{
        private String colStart;
        private String colSize;
        private String rowStart;
        private String rowSize;

        public String getColStart() {
            return colStart;
        }

        public void setColStart(String colStart) {
            this.colStart = colStart;
        }

        public String getColSize() {
            return colSize;
        }

        public void setColSize(String colSize) {
            this.colSize = colSize;
        }

        public String getRowStart() {
            return rowStart;
        }

        public void setRowStart(String rowStart) {
            this.rowStart = rowStart;
        }

        public String getRowSize() {
            return rowSize;
        }

        public void setRowSize(String rowSize) {
            this.rowSize = rowSize;
        }
    }

    private String fullName;
    private String name;
    private String type;
    private String bytes;
    private Boolean title;
    private Boolean autoSize;
    //    private Map<String, String> sizeTable;
    private SizeTable sizeTable;
//    private JsonObject sizeTable;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public Boolean getTitle() {
        return title;
    }

    public void setTitle(Boolean title) {
        this.title = title;
    }

    public Boolean getAutoSize() {
        return autoSize;
    }

    public void setAutoSize(Boolean autoSize) {
        this.autoSize = autoSize;
    }

//    public Map<String, String> getSizeTable() {
//        return sizeTable;
//    }
//
//    public void setSizeTable(Map<String, String> sizeTable) {
//        this.sizeTable = sizeTable;
//    }


    public SizeTable getSizeTable() {
        return sizeTable;
    }

    public void setSizeTable(SizeTable sizeTable) {
        this.sizeTable = sizeTable;
    }


//    public JsonObject getSizeTable() {
//        return sizeTable;
//    }
//
//    public void setSizeTable(JsonObject sizeTable) {
//        this.sizeTable = sizeTable;
//    }

}
