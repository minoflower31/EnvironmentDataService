package server;

import com.google.gson.annotations.SerializedName;

public class SignUpPost {
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private Integer status;

    public String getMsg() {
        return msg;
    }

    public Integer getStatus() {
        return status;
    }
}

