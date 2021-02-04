package com.example.envdataproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.ChartGet;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class ChartFragment extends Fragment {
    private Spinner spinner;
    private LineChart lineChart;
    private TextView locationText;

    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<Float> tempList = new ArrayList<>();

    private SharedPreferences sharedPref = null;
    private SharedPreferences.Editor editor = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chart, container, false);
        spinner = rootView.findViewById(R.id.chart_spinner);
        lineChart = rootView.findViewById(R.id.chart_line);
        locationText = rootView.findViewById(R.id.chart_locate_detail_tv);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.chart, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    //case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        if(locationText.getText().toString().isEmpty()) {
                            Toast toast = Toast.makeText(getContext(),"위치를 입력하세요.",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM,0,200);
                            toast.show();
                            spinner.setSelection(0);
                        } else {
                            //connectToServer();
                            getChart(timeList, tempList);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra("ChartFragment","chart");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //!!!!!
        //부가 설명 해주기
        if (locationText.getText().toString().isEmpty() || !locationText.getText().toString().isEmpty()) {
            locationText.setText(getSearchString());
        }
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class, getAccessToken());
        retrofitAPI.getChart(locationText.getText().toString(), spinner.getSelectedItemPosition()).enqueue(new Callback<ChartGet>() {
            @Override
            public void onResponse(Call<ChartGet> call, Response<ChartGet> response) {
                if (response.isSuccessful()) {
                    ChartGet block = response.body();

                    for (int i = 0; i < block.getData().size(); i++) {
                        timeList.add(block.getData().get(i).getTime());
                        tempList.add(block.getData().get(i).getTemp());
                    }

                    getChart(timeList, tempList);

                    Log.d("Server/Chart Success", block.getMsg());
                    Log.d("Server/Chart Success", block.getStatus());
                } else {
                    ResponseBody errBody = response.errorBody();
                    try {
                        Log.d("Server/Chart Success", errBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChartGet> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getChart(ArrayList<String> time, ArrayList<Float> temp) {
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        /*for (int i = 0; i < temp.size(); i++) {
            float val = (float)(Math.random() * temp.size())+30;
            values.add(new Entry(i, val));
        }*/
        ArrayList<String> mTime = new ArrayList<>();
        mTime.add("Jun-03\n00:20");
        mTime.add("Jun-03\n00:30");
        mTime.add("Jun-03\n00:35");
        mTime.add("Jun-03\n01:50");
        mTime.add("Jun-03\n02:20");
        mTime.add("Jun-03\n03:20");
        mTime.add("Jun-03\n03:30");
        mTime.add("Jun-03\n03:50");
        mTime.add("Jun-03\n04:00");
        mTime.add("Jun-03\n04:01");

        for(int i =0; i<mTime.size(); i++) {
            int val = (int)(Math.random() * mTime.size() + 30);
            values.add(new Entry(i,val));
        }

        LineDataSet set = new LineDataSet(values,"");
        set.setLineWidth(2.5f);
        set.setCircleHoleRadius(5.5f);
        set.setColor(Color.parseColor("#00C569"));
        set.setCircleHoleColor(Color.parseColor("#ffffff"));
        set.setCircleColor(Color.parseColor("#00C569"));
        set.setFillColor(Color.parseColor("#73F6B9"));
        set.setHighLightColor(Color.red(Color.BLACK));
        set.setDrawFilled(true);
        set.setDrawValues(true);
        set.setValueTextSize(13);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(set);

        LineData data = new LineData(dataSets);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(13);
        xAxis.setGranularity(1f);
        xAxis.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setTextSize(13);
        yAxis.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraBottomOffset(17);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mTime)); //수정 필요
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDescription(null);
        lineChart.setXAxisRenderer(new CustomXAxisRenderer(lineChart.getViewPortHandler(), lineChart.getXAxis(),lineChart.getTransformer(YAxis.AxisDependency.LEFT)));

        lineChart.setData(data);
        lineChart.setDragOffsetX(5);
        lineChart.setVisibleXRangeMaximum(5);
        lineChart.moveViewToX(mTime.size()); //수정 필요
        lineChart.animateY(2000, Easing.EaseInCirc);
        lineChart.invalidate();
    }

    private String getSearchString() {
        String str;
        sharedPref = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        str = sharedPref.getString("search_data1", "");
        editor.clear();
        editor.apply();
        return str;
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
        return token;
    }

}
