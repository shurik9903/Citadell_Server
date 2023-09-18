package org.example.data.mydata;

public class DWord {

    private Integer id;
    private String word;
    private Integer typeID;
    private Integer simpleID;
    private String description;

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

        if (word == null)
            word = "";

        this.word = word;
    }

    public Integer getTypeID() {
        return typeID;
    }

    public void setTypeID(Integer typeID) {

        if (typeID == null)
            typeID = -1;

        this.typeID = typeID;
    }

    public Integer getSimpleID() {
        return simpleID;
    }

    public void setSimpleID(Integer simpleID) {
        this.simpleID = simpleID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {this.description = description;}

}
