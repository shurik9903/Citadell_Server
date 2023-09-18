package org.example.data.mydata;


public class DDocData {

    public static class Data {
        private String type;
        private String index;
        private String select;
        private String message;
        private String classComment;
        private String analysisText;

        public String getClassComment() {
            return classComment;
        }

        public void setClassComment(String classComment) {
            this.classComment = classComment;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getSelect() {
            return select;
        }

        public void setSelect(String select) {
            this.select = select;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getAnalysisText() {
            return analysisText;
        }

        public void setAnalysisText(String analysisText) {
            this.analysisText = analysisText;
        }

    }

    private Data[] data;
    private String allSelect;

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public String getAllSelect() {
        return allSelect;
    }

    public void setAllSelect(String allSelect) {
        this.allSelect = allSelect;
    }

}
