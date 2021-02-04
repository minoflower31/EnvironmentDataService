package com.example.envdataproject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

class StatusBarColorForActivity {

    StatusBarColorForActivity(Activity activity, String string) {
        if(Build.VERSION.SDK_INT >=21) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
