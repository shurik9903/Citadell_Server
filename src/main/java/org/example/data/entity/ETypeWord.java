package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"types_words\"")
public class ETypeWord {

    @Id
    @Column(name = "\"id\"")
    private Integer id;

    @Column(name = "\"type\"")
    private String type;

    public ETypeWord(){};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}