package server;

import com.google.gson.annotations.SerializedName;

public class BoardAddPost {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
