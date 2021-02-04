package server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BoardAllGet {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<MyData> data;

    public List<MyData> getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public String getStatus() {
        return status;
    }

    public static class MyData {
        @SerializedName("idx")
        private Integer idx;
        @SerializedName("title")
        private String title;
        @SerializedName("author")
        private String author;
        @SerializedName("createdAt")
        private String createdAt;

        public String getAuthor() {
            return author;
        }

        public Integer getIdx() {
            return idx;
        }

        public String getTitle() {
            return title;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
