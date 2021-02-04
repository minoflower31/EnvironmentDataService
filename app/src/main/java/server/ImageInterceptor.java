package server;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ImageInterceptor implements Interceptor {
    private String autoToken;

    public ImageInterceptor(String token) {
        this.autoToken = token;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", autoToken).addHeader("Content-Type","image/jpeg");

        Request request = builder.build();
        return chain.proceed(request);
    }
}
