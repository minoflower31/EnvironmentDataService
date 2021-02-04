package com.example.envdataproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envdataproject.board.BoardAdapter;
import com.example.envdataproject.board.BoardItem;
import com.example.envdataproject.board.BoardWriteActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.BoardAllGet;
import server.RetrofitAPI;
import server.ServiceGenerator;

public class CommunityFragment extends Fragment {
    private ImageButton writeBtn, menuBtn;
    private TextView boardNameText, menuFreeBoardText, menuInfoBoardText;
    private RecyclerView recyclerView;

    private DrawerLayout drawerLayout;
    private View drawerView;

    Integer idx;
    String title,author, createdAt;

    private ArrayList<BoardItem> boardItems = new ArrayList<>();
    private BoardAdapter adapter = new BoardAdapter(boardItems);

    private SharedPreferences sharedPref=null;
    private SharedPreferences.Editor editor=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_community, container,false);
        writeBtn = rootView.findViewById(R.id.board_write_btn);
        menuBtn = rootView.findViewById(R.id.board_menu_btn);
        boardNameText = rootView.findViewById(R.id.board_tv);
        menuFreeBoardText = rootView.findViewById(R.id.board_menu_free_tv);
        menuInfoBoardText = rootView.findViewById(R.id.borad_menu_info_tv);
        recyclerView = rootView.findViewById(R.id.board_recycler);
        drawerLayout = rootView.findViewById(R.id.board_drawer_layout);
        drawerView = rootView.findViewById(R.id.board_drawer);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        isClickMenuInText(false);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BoardWriteActivity.class);
                intent.putExtra("boardName",boardNameText.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        menuFreeBoardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickMenuInText(true);
                boardNameText.setText(menuFreeBoardText.getText().toString());
                connectToServer();
                boardItems.clear();
            }
        });

        menuInfoBoardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickMenuInText(true);
                boardNameText.setText(menuInfoBoardText.getText().toString());
                connectToServer();
                boardItems.clear();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        StatusBarColorForActivity statusBarColor = new StatusBarColorForActivity(getActivity(), "#fcfcfc");
    }

    private void isClickMenuInText(boolean bool) {
        if(!bool) {
            writeBtn.setVisibility(View.INVISIBLE);
            boardNameText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            writeBtn.setVisibility(View.VISIBLE);
            boardNameText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        drawerLayout.closeDrawer(drawerView);
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGenerator.createService(RetrofitAPI.class,getAccessToken());

        retrofitAPI.getBoardAll(boardNameText.getText().toString()).enqueue(new Callback<BoardAllGet>() {
            @Override
            public void onResponse(Call<BoardAllGet> call, Response<BoardAllGet> response) {
                if(response.isSuccessful()) {
                    BoardAllGet block = response.body();

                    for(int i=0; i<block.getData().size(); i++) {
                        idx = block.getData().get(i).getIdx();
                        title = block.getData().get(i).getTitle();
                        author = block.getData().get(i).getAuthor();
                        createdAt = block.getData().get(i).getCreatedAt();

                        BoardItem item = new BoardItem(title,author,formattingDate(createdAt),idx,boardNameText.getText().toString());
                        boardItems.add(item);
                    }
                    adapter.notifyDataSetChanged();

                    Log.d("Server/BoardAllGet", block.getMsg());
                    Log.d("Server/BoardAllGet", block.getStatus());
                } else {
                    ResponseBody errBody = response.errorBody();
                    try {
                        Log.d("Server/BoardAllGet", errBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardAllGet> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String formattingDate(String sDate) {
        try {
            @SuppressLint("SimpleDateFormat") Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sDate);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (date != null) {
                return simpleDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
