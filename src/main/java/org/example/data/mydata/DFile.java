package org.example.data.mydata;

import org.example.data.entity.EFile;
import org.example.data.entity.ELogin;

public class DFile {

    private Integer file_id;
    private String file_name;
    private byte[] file_byte;
    private Integer file_userid;
    private String Msg = "";

    public DFile(){}

    public DFile(String msg){
        this.Msg = msg;
    }

    public DFile(int file_id, String file_name, byte[] file_byte, Integer file_userid){
        this.file_id = file_id;
        this.file_name = file_name;
        this.file_byte = file_byte;
        this.file_userid = file_userid;
    }

    public DFile(EFile file){
        this.file_id = file.getFile_id();
        this.file_name = file.getFile_name();
        this.file_byte = file.getFile_byte();
        this.file_userid = file.getFile_userid();
    }

    public Integer getFile_id() {
        return file_id;
    }

    public void setFile_id(Integer file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public byte[] getFile_byte() {
        return file_byte;
    }

    public void setFile_byte(byte[] file_byte) {
        this.file_byte = file_byte;
    }

    public Integer getFile_userid() {
        return file_userid;
    }

    public void setFile_userid(Integer file_userid) {
        this.file_userid = file_userid;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}

