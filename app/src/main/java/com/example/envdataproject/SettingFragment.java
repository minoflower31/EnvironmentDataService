package com.example.envdataproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.Logout;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class SettingFragment extends Fragment {
    RelativeLayout logoutLayout, deleteLayout;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref1=null;
    private SharedPreferences.Editor editor1=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container,false);
        logoutLayout = rootView.findViewById(R.id.setting_logout_layout);
        deleteLayout = rootView.findViewById(R.id.setting_delete_layout);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServerLogout();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void connectToServerLogout() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class, getAccessToken());
        retrofitAPI.postLogout().enqueue(new Callback<Logout>() {
            @Override
            public void onResponse(Call<Logout> call, Response<Logout> response) {
                if(response.isSuccessful()) {
                    Logout block = response.body();

                    Log.d("Server/Logout Success", block.getMsg());
                    Log.d("Server/Logout Success", block.getStatus());

                    resetAccessToken();
                    getAutoLogin();
                    getActivity().finish();

                } else {
                    ResponseBody errBody = response.errorBody();
                    try {
                        Log.d("Server/Logout Fail", errBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Logout> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void connectToServerDelete() {

    }

    private String getAccessToken() {
        String token;
        sharedPref = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        token = sharedPref.getString("access_token", "");
        return token;
    }

    public String getRefreshToken() {
        String token;
        sharedPref = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        token = sharedPref.getString("refresh_token", "");
        editor.apply();
        return token;
    }

    private void resetAccessToken() {
        sharedPref = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString("access_token", null);
        editor.putString("refresh_token", null);

        editor.apply();
    }

    private void getAutoLogin() {
        sharedPref1 = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor1 = sharedPref1.edit();

        editor1.clear();
        editor1.apply();
    }
}
