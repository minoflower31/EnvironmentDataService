package server;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface RetrofitAPI {
    @FormUrlEncoded
    @POST("/api/user/signin")
    Call<SignInPost> postSignIn(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/api/user")
    Call<SignUpPost> postSignUp(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/api/envdata/all")
    Call<HomeDataAllGet> getHomeDataAll(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/api/envdata/one")
    Call<HomeDataOneGet> getHomeDataOne(@Field("idx") String idx);

    @Multipart
    @POST("/api/envdata")
    Call<AddDataPost> postAddData(@PartMap Map<String, RequestBody> fields, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("/api/post/all")
    Call<BoardAllGet> getBoardAll(@Field("boardName") String boardName);

    @FormUrlEncoded
    @POST("/api/post/one")
    Call<BoardOneGet> getBoardOne(@Field("idx") Integer idx, @Field("boardName") String boardName);

    @FormUrlEncoded
    @POST("/api/post")
    Call<BoardAddPost> postBoardAdd(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/api/envdata/chart")
    Call<ChartGet> getChart(@Field("location") String location, @Field("selection") Integer selection);

    @POST("/api/user/logout")
    Call<Logout> postLogout();
}