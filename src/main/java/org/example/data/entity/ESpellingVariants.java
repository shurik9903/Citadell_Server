package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"spelling_variants\"")
public class ESpellingVariants {

    @Id
    @Column(name = "\"id\"")
    private Integer id;

    @Column(name = "\"word\"")
    private String word;

    @Column(name = "\"simple_id\"")
    private Integer simple_id;

    @Column(name = "\"description\"")
    private String description;

    public ESpellingVariants(){};

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

    public Integer getSimple_id() {
        return simple_id;
    }

    public void setSimple_id(Integer simple_id) {
        this.simple_id = simple_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}