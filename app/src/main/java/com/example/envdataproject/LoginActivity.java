package com.example.envdataproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import server.RetrofitAPI;
import server.SignInPost;

import static java.lang.String.valueOf;

public class LoginActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://13.125.77.193:8080";
    EditText emailEdit, pwEdit;
    Button continueBtn;
    TextView signUpText, alertText;
    CheckBox autoLoginCheckBox;
    Boolean loginChecked;

    private SharedPreferences sharedPref = null;
    private SharedPreferences.Editor editor = null;
    SharedPreferences sharedPref1;
    SharedPreferences.Editor editor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StatusBarColorForActivity statusBarColor = new StatusBarColorForActivity(this, "#ffffff");

        emailEdit = findViewById(R.id.sign_up_id);
        pwEdit = findViewById(R.id.sign_up_pw);
        continueBtn = findViewById(R.id.sign_up_complete_btn);
        signUpText = findViewById(R.id.sign_in_sign_up_tv);
        alertText = findViewById(R.id.sign_in_alert_tv);
        autoLoginCheckBox = findViewById(R.id.sign_in_auto_login_box);

        sharedPref1 = getSharedPreferences("setting", MODE_PRIVATE);
        editor1 = sharedPref1.edit();

        if (sharedPref1.getBoolean("autoLogin", false)) {
            emailEdit.setText(sharedPref1.getString("ID", ""));
            pwEdit.setText(sharedPref1.getString("PW", ""));
            autoLoginCheckBox.setChecked(true);
            connectToServer();
        }

        autoLoginCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String id = emailEdit.getText().toString();
                    String pw = pwEdit.getText().toString();

                    editor1.putString("ID", id);
                    editor1.putString("PW", pw);
                    editor1.putBoolean("autoLogin", true);
                    editor1.apply();
                } else {
                    editor1.clear();
                    editor1.commit();
                }
            }
        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
    }

    private void connectToServer() {
        //서버와 통신하기 위한 일종의 장비
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //retofit과 연결하는 API다
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        HashMap<String, Object> input = new HashMap<>();
        input.put("email", emailEdit.getText().toString());
        input.put("pwd", pwEdit.getText().toString());

        retrofitAPI.postSignIn(input).enqueue(new Callback<SignInPost>() {
            @Override
            public void onResponse(Call<SignInPost> call, Response<SignInPost> response) {
                if (response.isSuccessful()) {
                    SignInPost block = response.body();
                    Log.d("Server/SignInPost", valueOf(block.getStatus()));
                    Log.d("Server/SignInPost", block.getMsg());

                    sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
                    editor = sharedPref.edit();

                    editor.putString("access_token", block.getData().getAccessToken());
                    editor.putString("refresh_token", block.getData().getRefreshToken());
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    alertText.setVisibility(View.INVISIBLE);

                } else {
                    Converter<ResponseBody, SignInPost> errorConverter = retrofit.responseBodyConverter(SignInPost.class, new Annotation[0]);
                    try {
                        SignInPost error = null;
                        if (response.errorBody() != null) {
                            error = errorConverter.convert(response.errorBody());

                            if (error.getStatus().equals("401")) {
                                alertText.setText("잘못된 비밀번호입니다.");
                                alertText.setVisibility(View.VISIBLE);
                            } else if (error.getStatus().equals("404")) {
                                alertText.setText("등록되지 않은 계정입니다.");
                                alertText.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("Server/SignIn", error.getMsg());
                                Log.d("Server/SignIn", error.getStatus());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignInPost> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
