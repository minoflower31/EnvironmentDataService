package server;

import com.google.gson.annotations.SerializedName;

public class SignInPost {
    @SerializedName("data")
    private MyData data;
    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;

    public MyData getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public String getStatus() {
        return status;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    public static class MyData {
        @SerializedName("accessToken")
        private String accessToken;

        @SerializedName("refreshToken")
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }
}
