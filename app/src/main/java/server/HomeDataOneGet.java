package server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeDataOneGet {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private MyData data;

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public MyData getData() {
        return data;
    }

    public static class MyData {
        @SerializedName("idx")
        private Integer idx;
        @SerializedName("location")
        private String location;
        @SerializedName("time")
        private String time;
        @SerializedName("picture")
        private String picture;
        @SerializedName("description")
        private String description;
        @SerializedName("tags")
        private List<MyTag> tags;
        @SerializedName("temp")
        private Float temp;
        @SerializedName("humid")
        private Float humid;
        @SerializedName("dust")
        private Integer dust;
        @SerializedName("atm")
        private Float atm;
        @SerializedName("author")
        private String author;

        public Integer getIdx() {
            return idx;
        }

        public String getLocation() {
            return location;
        }

        public String getTime() {
            return time;
        }

        public String getPicture() {
            return picture;
        }

        public String getDescription() {
            return description;
        }

        public List<MyTag> getTags() {
            return tags;
        }

        public Float getTemp() {
            return temp;
        }

        public Float getHumid() {
            return humid;
        }

        public Integer getDust() {
            return dust;
        }

        public Float getAtm() {
            return atm;
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
