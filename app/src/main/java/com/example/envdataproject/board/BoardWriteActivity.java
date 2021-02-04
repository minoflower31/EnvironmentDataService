package com.example.envdataproject.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.envdataproject.R;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.BoardAddPost;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class BoardWriteActivity extends AppCompatActivity {
    ImageButton backBtn, completeBtn;
    EditText titleEdit, bodyEdit;
    String boardName;

    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        backBtn = findViewById(R.id.board_write_back_btn);
        completeBtn = findViewById(R.id.board_write_complete_btn);
        titleEdit = findViewById(R.id.board_write_title_et);
        bodyEdit = findViewById(R.id.board_write_body_et);

        Intent intent = getIntent();
        boardName = intent.getStringExtra("boardName");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class, getAccessToken());
        HashMap<String, Object> input = new HashMap<>();
        input.put("title", titleEdit.getText().toString());
        input.put("content", bodyEdit.getText().toString());
        input.put("boardName", boardName);
        retrofitAPI.postBoardAdd(input).enqueue(new Callback<BoardAddPost>() {
            @Override
            public void onResponse(Call<BoardAddPost> call, Response<BoardAddPost> response) {
                if(response.isSuccessful()) {
                    BoardAddPost block = response.body();
                    Log.d("Server/Add Success", block.getMsg());
                    Log.d("Server/Add Success", block.getStatus());

                    onBackPressed();
                } else {
                    ResponseBody errBody = response.errorBody();
                    try {
                        Log.d("Server/Add Fail", errBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardAddPost> call, Throwable t) {
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
}
