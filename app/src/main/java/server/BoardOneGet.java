package server;

import com.google.gson.annotations.SerializedName;

public class BoardOneGet {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private MyData data;

    public MyData getData() {
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
        @SerializedName("content")
        private String content;
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

        public String getContent() {
            return content;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
