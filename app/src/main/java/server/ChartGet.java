package server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChartGet {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<MyData> data;

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public List<MyData> getData() {
        return data;
    }

    public static class MyData {
        @SerializedName("time")
        private String time;
        @SerializedName("temp")
        private Float temp;

        public String getTime() {
            return time;
        }

        public Float getTemp() {
            return temp;
        }
    }
}
