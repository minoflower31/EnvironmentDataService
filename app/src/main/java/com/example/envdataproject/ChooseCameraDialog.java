package com.example.envdataproject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChooseCameraDialog {
    private Context mContext;
    RelativeLayout camera_layout;
    RelativeLayout photo_layout;
    Dialog dialog;
    ChooseCameraDialog(Context context) {
        this.mContext = context;
    }

    void callFunction() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_camera_gallery_choice);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.show();

        camera_layout = dialog.findViewById(R.id.camera_choose_cam_layout);
        photo_layout = dialog.findViewById(R.id.camera_choose_photo_layout);
        TextView cancelBtn = dialog.findViewById(R.id.camera_cancel_tv);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
