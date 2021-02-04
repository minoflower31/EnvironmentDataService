package com.example.envdataproject.board;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envdataproject.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.BoardOneGet;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class BoardDetailActivity extends AppCompatActivity {
    ImageButton backBtn;
    TextView name, date, title, body;
    Integer idx;
    String boardName;

    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        backBtn = findViewById(R.id.board_free_detail_back_btn);
        name = findViewById(R.id.board_free_detail_name);
        date = findViewById(R.id.board_free_detail_date);
        title = findViewById(R.id.board_free_detail_title);
        body = findViewById(R.id.board_free_detail_body);

        Intent intent = getIntent();
        idx = intent.getIntExtra("idx",0);
        boardName = intent.getStringExtra("boardName");

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
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class, getAccessToken());
        retrofitAPI.getBoardOne(idx,boardName).enqueue(new Callback<BoardOneGet>() {
            @Override
            public void onResponse(Call<BoardOneGet> call, Response<BoardOneGet> response) {
                if(response.isSuccessful()) {
                    BoardOneGet block = response.body();

                    Log.d("Server/BoardOneGet", block.getMsg());
                    Log.d("Server/BoardOneGet",block.getStatus());

                    name.setText(block.getData().getAuthor());
                    date.setText(formattingDate(block.getData().getCreatedAt()));
                    title.setText(block.getData().getTitle());
                    body.setText(block.getData().getContent());

                } else {
                    ResponseBody errBody = response.errorBody();
                    try {
                        Log.d("Server/BoardOneGet Err", errBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardOneGet> call, Throwable t) {
                t.printStackTrace();
            }
        });
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

    private String formattingDate(String sDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).parse(sDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault());
            if (date != null) {
                return simpleDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
