package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"user_tables\"")
public class ENameFiles {

    @Id
    @Column(name = "\"id\"")
    private Integer file_id;

    @Column(name = "\"name\"")
    private String file_name;

    @Column(name = "\"userid\"")
    private int file_userid;

    public ENameFiles(){};

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

    public int getFile_userid() {
        return file_userid;
    }

    public void setFile_userid(int file_userid) {
        this.file_userid = file_userid;
    }

}
