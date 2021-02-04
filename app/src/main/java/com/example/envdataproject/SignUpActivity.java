package com.example.envdataproject;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import server.RetrofitAPI;
import server.SignUpPost;

public class SignUpActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://13.125.77.193:8080";

    EditText name, email, password, passwordConfirm;
    TextView confirmText, overlapEmailText;
    Button completeBtn;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        StatusBarColorForActivity statusBarColor = new StatusBarColorForActivity(this, "#ffffff");

        name = findViewById(R.id.sign_up_name);
        email = findViewById(R.id.sign_up_id);
        password = findViewById(R.id.sign_up_pw);
        passwordConfirm = findViewById(R.id.sign_up_pw_confirm);
        confirmText = findViewById(R.id.sign_up_pw_is_correct);
        overlapEmailText = findViewById(R.id.sign_up_email_incorrect);
        completeBtn = findViewById(R.id.sign_up_complete_btn);
        backBtn = findViewById(R.id.sign_up_back_btn);

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        completeBtn.setOnClickListener(view -> {
            if(passwordConfirm.getText().toString().equals("비밀번호가 일치합니다.")) {
                connectToServer();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordConfirmFunc(password, passwordConfirm);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordConfirmFunc(password, passwordConfirm);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void passwordConfirmFunc(EditText editText1, EditText editText2) {
        if (!editText1.getText().toString().equals(editText2.getText().toString())) {
            confirmText.setVisibility(View.VISIBLE);
            confirmText.setText("비밀번호가 일치하지 않습니다.");
            confirmText.setTextColor(Color.parseColor("#D50000"));
        } else {
            confirmText.setVisibility(View.VISIBLE);
            confirmText.setText("비밀번호가 일치합니다.");
            confirmText.setTextColor(Color.parseColor("#00C853"));
        }

        if (editText1.getText().toString().equals("") && editText2.getText().toString().equals("")) {
            confirmText.setVisibility(View.INVISIBLE);
        }

        if (editText2.getText().toString().equals("")) {
            confirmText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void connectToServer() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        HashMap<String, Object> input = new HashMap<>();
        input.put("email", email.getText().toString());
        input.put("name", name.getText().toString());
        input.put("pwd", password.getText().toString());

        retrofitAPI.postSignUp(input).enqueue(new Callback<SignUpPost>() {
            @Override
            public void onResponse(Call<SignUpPost> call, Response<SignUpPost> response) {
                if (response.isSuccessful()) {
                    SignUpPost block = response.body();

                    Log.d("Server/SignUpPost", String.valueOf(block.getStatus()));
                    Log.d("Server/SignUpPost", block.getMsg());

                    overlapEmailText.setVisibility(View.INVISIBLE);
                    onBackPressed();
                } else {
                    Gson gson = new Gson();
                    SignUpPost error = null;
                    try {
                        error = gson.fromJson(response.errorBody().string(), SignUpPost.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (error.getStatus() == 409) {
                        overlapEmailText.setText("중복된 계정입니다.");
                        overlapEmailText.setVisibility(View.VISIBLE);
                    } else if (error.getStatus() == 400) {
                        Toast toast = Toast.makeText(getApplicationContext(), "빠짐없이 빈칸을 입력해주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }

            @Override
            public void onFailure(Call<SignUpPost> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
