package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"simple_words\"")
public class ESimpleWord {

    @Id
    @Column(name = "\"id\"")
    private Integer id;

    @Column(name = "\"word\"")
    private String word;

    @Column(name = "\"type_id\"")
    private Integer type_id;

    public ESimpleWord(){};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getType_id() {
        return type_id;
    }

    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }
}
