package server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeDataAllGet {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private Integer status;
    @SerializedName("data")
    private List<MyData> data;

    public String getMsg() {
        return msg;
    }

    public Integer getStatus() {
        return status;
    }

    public List<MyData> getData() {
        return data;
    }
    //List --> 대괄호
    //클래스 명 --> 중괄호

    public static class MyData {
        @SerializedName("idx")
        private Integer idx;
        @SerializedName("time")
        private String time;
        @SerializedName("tags")
        private List<MyTag> tags;
        @SerializedName("author")
        private String author;

        public Integer getIdx() {
            return idx;
        }

        public String getTime() {
            return time;
        }

        public List<MyTag> getTags() {
            return tags;
        }

        public String getAuthor() {
            return author;
        }

        public static class MyTag {
            @SerializedName("idx")
            private Integer idx;
            @SerializedName("value")
            private String value;

            public Integer getIdx() {
                return idx;
            }

            public String getValue() {
                return value;
            }
        }
    }
}
