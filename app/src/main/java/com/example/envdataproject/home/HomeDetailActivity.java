package com.example.envdataproject.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envdataproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.HomeDataOneGet;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class HomeDetailActivity extends AppCompatActivity {
    ImageButton backBtn;
    TextView locateText, dateText, timeText, descriptionText, tempText, humidText, dustText, atmText, tagText, authorText, notExistPhotoText;
    ImageView imageView;
    Integer idx;
    Bitmap bitmap;
    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);

        backBtn = findViewById(R.id.home_back_btn);
        locateText = findViewById(R.id.home_locate_detail_tv);
        dateText = findViewById(R.id.home_date_detail_tv);
        timeText = findViewById(R.id.home_time_detail_tv);
        authorText = findViewById(R.id.home_detail_author_tv);
        tagText = findViewById(R.id.home_tag_detail_tv);
        descriptionText = findViewById(R.id.home_description_detail_tv);
        tempText = findViewById(R.id.home_temp_detail_tv);
        humidText = findViewById(R.id.home_humid_detail_tv);
        dustText = findViewById(R.id.home_dust_detail_tv);
        atmText = findViewById(R.id.home_atm_detail_tv);
        notExistPhotoText = findViewById(R.id.home_no_photo_tv);
        imageView = findViewById(R.id.home_detail_photo);

        Intent intent = getIntent();
        idx = intent.getIntExtra("idx", 0);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        connectToServer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class, getAccessToken());
        retrofitAPI.getHomeDataOne(idx.toString()).enqueue(new Callback<HomeDataOneGet>() {
            @Override
            public void onResponse(Call<HomeDataOneGet> call, Response<HomeDataOneGet> response) {
                if (response.isSuccessful()) {
                    HomeDataOneGet block = response.body();
                    ArrayList<String> list = new ArrayList<>();

                    Log.d("Server/HomeDataOneGet", block.getStatus());
                    Log.d("Server/HomeDataOneGet", block.getMsg());

                    String dateAndTime = block.getData().getTime();
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateAndTime);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());

                        if (date != null) {
                            dateText.setText(dateFormat.format(date));
                            timeText.setText(timeFormat.format(date));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    locateText.setText(block.getData().getLocation());
                    descriptionText.setText(block.getData().getDescription());
                    authorText.setText(block.getData().getAuthor());
                    for (int i = 0; i < block.getData().getTags().size(); i++) {
                        list.add(block.getData().getTags().get(i).getValue());
                    }
                    if (!block.getData().getPicture().isEmpty()) {
                        setImageThread(block.getData().getPicture(), imageView);
                    } else {
                        notExistPhotoText.setVisibility(View.VISIBLE);
                    }
                    tagText.setText(String.valueOf(list));
                    tempText.setText(String.valueOf(block.getData().getTemp()));
                    humidText.setText(String.valueOf(block.getData().getHumid()));
                    dustText.setText(String.valueOf(block.getData().getDust()));
                    atmText.setText(String.valueOf(block.getData().getAtm()));
                } else {
                    ResponseBody err = response.errorBody();
                    try {
                        Log.d("Server/HomeDataOneGet", err.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeDataOneGet> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setImageThread(final String urlStr, ImageView imageView) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            imageView.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getAccessToken() {
        String token;
        sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        token = sharedPref.getString("access_token", "");
        return token;
    }

    public String getRefreshToken() {
        String token;
        sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        token = sharedPref.getString("refresh_token", "");
        return token;
    }
}
