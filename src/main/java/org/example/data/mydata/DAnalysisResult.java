package org.example.data.mydata;

import java.util.ArrayList;

public class DAnalysisResult {

    public static class AnalysisRows{
        private int number;
        private String comment;
        private int class_comment;
        private int percent;

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }


        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getClass_comment() {
            return class_comment;
        }

        public void setClass_comment(int class_comment) {
            this.class_comment = class_comment;
        }
    }

    private String Message;
    private String Model;
    private ArrayList<AnalysisRows> comments;

    public ArrayList<AnalysisRows> getComments() {
        return comments;
    }

    public void setComments(ArrayList<AnalysisRows> comments) {
        this.comments = comments;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

}
