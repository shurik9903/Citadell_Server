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
    private Integer reportID;

    @Column(name = "\"table_id\"")
    private Integer tableID;

    @Column(name = "\"user_id\"")
    private Integer userID;

    @Column(name = "\"message\"")
    private String message;

    @Column(name = "\"row_num\"")
    private Integer row_num;

    public EReport(){}

    public Integer getReportID() {
        return reportID;
    }

    public void setReportID(Integer reportID) {
        this.reportID = reportID;
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

    public Integer getRow_num() {
        return row_num;
    }

    public void setRow_num(Integer row_num) {
        this.row_num = row_num;
    }


}