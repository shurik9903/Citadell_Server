package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "\"user_tables\"")
public class EFile implements Serializable {

    private String msg = "";

    @Id
    @Column(name = "\"id\"")
    private Integer file_id;

    @Column(name = "\"name\"")
    private String file_name;

    @Column(name = "\"file\"")
    private byte[] file_byte;

    @Column(name = "\"userid\"")
    private int file_userid;

    public EFile(){}

    public EFile(String msg){
        this.msg = msg;
    }

    public EFile(String file_name, byte[] file_byte, int file_userid){
        this.file_name = file_name;
        this.file_byte = file_byte;
        this.file_userid = file_userid;
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

    public int getFile_userid() {
        return file_userid;
    }

    public void setFile_userid(int file_userid) {
        this.file_userid = file_userid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}