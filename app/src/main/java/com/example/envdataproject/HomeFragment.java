package com.example.envdataproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envdataproject.home.HomeAdapter;
import com.example.envdataproject.home.HomeItem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.HomeDataAllGet;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class HomeFragment extends Fragment {
    private ArrayList<HomeItem> list = new ArrayList<>();
    private HomeAdapter adapter = new HomeAdapter(list);
    private RecyclerView recyclerView;

    private Button searchBtn;
    private TextView locateText, dateText, timeText;
    String addressStr;

    String dateServerStr, timeServerStr;
    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor editor = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        locateText = rootView.findViewById(R.id.home_locate_detail_tv);
        timeText = rootView.findViewById(R.id.home_time_detail_tv);
        searchBtn = rootView.findViewById(R.id.home_search_btn);
        dateText = rootView.findViewById(R.id.home_date_detail_tv);
        recyclerView = rootView.findViewById(R.id.home_recycler_view);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
                list.clear();
            }
        });

        locateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MapActivity.class);
                startActivity(intent);
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showDate();
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime();
            }
        });

        Intent intent = getActivity().getIntent();
        addressStr = intent.getStringExtra("searchStr");
        if (addressStr != null) {
            locateText.setText(addressStr);
        }
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class, getAccessToken());
        HashMap<String, Object> input = new HashMap<>();
        input.put("location", locateText.getText().toString());
        input.put("date", dateServerStr);
        input.put("time", timeServerStr);

        retrofitAPI.getHomeDataAll(input).enqueue(new Callback<HomeDataAllGet>() {
            @Override
            public void onResponse(@NotNull Call<HomeDataAllGet> call, @NotNull Response<HomeDataAllGet> response) {
                if (response.isSuccessful()) {
                    HomeDataAllGet block = response.body();

                    Log.d("Server/HomeDataAllGet", String.valueOf(block.getStatus()));
                    Log.d("Server/HomeDataAllGet", block.getMsg());

                    for (int i = 0; i < block.getData().size(); i++) {
                        Integer idx;
                        String time, author;
                        ArrayList<String> value = new ArrayList<>();
                        idx = block.getData().get(i).getIdx();
                        time = block.getData().get(i).getTime();
                        for (int j = 0; j < block.getData().get(i).getTags().size(); j++) {
                            value.add(block.getData().get(i).getTags().get(j).getValue());
                            //1. getData: 클래스(중괄호) 로 불러오고
                            //2. get(i) : 태그를 하나하나씩 불러온다
                            //3. getTages() : 저장된 태그를 가져온다.

                        }
                        author = block.getData().get(i).getAuthor();
                        HomeItem item = new HomeItem(formattingDate(time),value, author,idx);
                        list.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    ResponseBody error = response.errorBody();
                    try {
                        System.out.println(error.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<HomeDataAllGet> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDate() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String dateStr = i + "년 " + (i1 + 1) + "월 " + i2 + "일";
                dateText.setText(dateStr);

                Calendar calendar = Calendar.getInstance();
                calendar.set(i,i1,i2);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                dateServerStr = format.format(calendar.getTime());
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(getContext(), listener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTime() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String timeStr = i + "시";
                timeText.setText(timeStr);

                timeServerStr = String.valueOf(i);
            }
        };

        long now = System.currentTimeMillis(); //현재 시간 불러옴
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormatHour = new SimpleDateFormat("HH");
        @SuppressLint("SimpleDateFormat") DateFormat dateFormatMinute = new SimpleDateFormat("mm");
        String hour = dateFormatHour.format(date);
        String minute = dateFormatMinute.format(date);
        TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, Integer.parseInt(hour), Integer.parseInt(minute), false);

        dialog.show();
    }

    private String formattingDate(String sDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(sDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAccessToken() {
        String token;
        sharedPref = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        token = sharedPref.getString("access_token", "");
        return token;
    }

    public String getRefreshToken() {
        String token;
        sharedPref = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        token = sharedPref.getString("refresh_token", "");
        return token;
    }
}
