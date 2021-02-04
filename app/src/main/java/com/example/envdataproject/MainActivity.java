package com.example.envdataproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarColorForActivity statusBarColor = new StatusBarColorForActivity(this, "#fcfcfc");

        navigationView = findViewById(R.id.bottom_navigation);

        navigationView.setVisibility(View.VISIBLE);
        replaceFragment(new HomeFragment());
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navi_home_btn:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.navi_chart_btn:
                        replaceFragment(new ChartFragment());
                        break;
                    case R.id.navi_upload_btn:
                        replaceFragment(new AddFragment());
                        break;
                    case R.id.navi_community_btn:
                        replaceFragment(new CommunityFragment());
                        break;
                    case R.id.navi_setting_btn:
                        replaceFragment(new SettingFragment());
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (navigationView.getSelectedItemId() == R.id.navi_home_btn) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            super.onBackPressed();
        } else {
            if(navigationView.getSelectedItemId() ==R.id.navi_upload_btn) {
                navigationView.setVisibility(View.VISIBLE);
            }
            navigationView.setSelectedItemId(R.id.navi_home_btn);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if(fragment.getClass()==HomeFragment.class) {
            ft.replace(R.id.main_frame_layout, fragment,"home");
        }
        if(fragment.getClass()==ChartFragment.class) {
            ft.replace(R.id.main_frame_layout, fragment,"chart");

        }
        if(fragment.getClass()==AddFragment.class) {
            ft.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_down);
            ft.replace(R.id.main_frame_layout, fragment,"add");
            navigationView.setVisibility(View.GONE);
        }
        if(fragment.getClass()==CommunityFragment.class) {
            ft.replace(R.id.main_frame_layout, fragment,"community");

        }
        if(fragment.getClass()==SettingFragment.class) {
            ft.replace(R.id.main_frame_layout, fragment,"setting");
        }

        ft.commit();
    }
}
