package server;

import com.google.gson.annotations.SerializedName;

public class Logout {
    @SerializedName("status")
    private String status;
    @SerializedName("msg")
    private String msg;

    public String getMsg() {
        return msg;
    }

    public String getStatus() {
        return status;
    }
}
