package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "\"user_reports\"")
public class EReport implements Serializable {

    @Id
    @Column(name = "\"id\"")
    private Integer fileID;

    @Column(name = "\"table_id \"")
    private Integer tableID;

    @Column(name = "\"user_id \"")
    private Integer userID;

    @Column(name = "\"message\"")
    private String message;

    @Column(name = "\"row\"")
    private Integer row;

    public EReport(){}

    public Integer getFileID() {
        return fileID;
    }

    public void setFileID(Integer fileID) {
        this.fileID = fileID;
    }

    public Integer getTableID() {
        return tableID;
    }

    public void setTableID(Integer tableID) {
        this.tableID = tableID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

}