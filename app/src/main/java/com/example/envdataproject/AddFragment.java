package com.example.envdataproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import server.AddDataPost;
import server.RetrofitAPI;
import server.ServiceGeneratorWithImg;

public class AddFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private static final int FROM_CAMERA = 2;

    private ImageButton backBtn, helpBtn;
    private Button uploadBtn;
    private RelativeLayout locateLayout, photoLayout;
    private TextView locateText, dateText, timeText, photoAddText, tempText, humidText, dustText, o3Text;
    ;
    private ImageView imageView, plusImg;
    private CheckBox airCheckBox, waterCheckBox, soilCheckBox, roadCheckBox, etcCheckBox;
    private EditText descEdit;

    private ChooseCameraDialog dialog;

    Uri uri, imgUri;
    String filePath;
    private String photoCurrentPath;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialog = new ChooseCameraDialog(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedSearch();
        initSharedEnvData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_add, container, false);
        backBtn = rootView.findViewById(R.id.add_back_btn);
        uploadBtn = rootView.findViewById(R.id.add_upload_btn);
        locateLayout = rootView.findViewById(R.id.add_layout_1);
        photoLayout = rootView.findViewById(R.id.add_layout_4);
        locateText = rootView.findViewById(R.id.add_locate_detail_tv);
        dateText = rootView.findViewById(R.id.add_date_detail_tv);
        timeText = rootView.findViewById(R.id.add_time_detail_tv);
        photoAddText = rootView.findViewById(R.id.add_photo_plus_tv);
        imageView = rootView.findViewById(R.id.add_photo_image_view);
        plusImg = rootView.findViewById(R.id.add_photo_plus_img);
        helpBtn = rootView.findViewById(R.id.add_help_btn);

        airCheckBox = rootView.findViewById(R.id.add_tag_check_box1);
        waterCheckBox = rootView.findViewById(R.id.add_tag_check_box2);
        soilCheckBox = rootView.findViewById(R.id.add_tag_check_box3);
        roadCheckBox = rootView.findViewById(R.id.add_tag_check_box4);
        etcCheckBox = rootView.findViewById(R.id.add_tag_check_box5);

        descEdit = rootView.findViewById(R.id.add_description_et);
        tempText = rootView.findViewById(R.id.add_temp_detail_tv);
        humidText = rootView.findViewById(R.id.add_humid_detail_tv);
        dustText = rootView.findViewById(R.id.add_dust_detail_tv);
        o3Text = rootView.findViewById(R.id.add_o3_detail_tv);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //위치 설정하기
        locateLayout.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            intent.putExtra("AddFragment", "add");
            startActivity(intent);
        });

        //사진 추가 클릭 시
        photoLayout.setOnClickListener(view12 -> {
            setPermission();
            makeDialog();
        });

        helpBtn.setOnClickListener(view13 -> showHelpDialog());

        //뒤로 가기 버튼
        backBtn.setOnClickListener(view14 -> {
            getActivity().onBackPressed();
            getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).remove(AddFragment.this).commit();
        });

        //업로드 버튼
        uploadBtn.setOnClickListener(view15 -> {
            if (locateText.getText().toString().isEmpty()) {
                Toast toast1 = Toast.makeText(getContext(), "위치를 설정해주세요.", Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.BOTTOM, 0, 250);
                toast1.show();
            } else if (!airCheckBox.isChecked() && !waterCheckBox.isChecked() && !soilCheckBox.isChecked() &&
                    !roadCheckBox.isChecked() && !etcCheckBox.isChecked()) {
                Toast toast1 = Toast.makeText(getContext(), "체크 박스를 한 개 이상 설정해주세요.", Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.BOTTOM, 0, 250);
                toast1.show();
            } else {
                connectToServer();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locateText.getText().toString().isEmpty() || !locateText.getText().toString().isEmpty()) {
            locateText.setText(getSearchString());
        }

        if (!locateText.getText().toString().isEmpty()) {
            new Thread() {
                @Override
                public void run() {
                    //온도, 습도
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    @SuppressLint("SimpleDateFormat") DateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
                    @SuppressLint("SimpleDateFormat") DateFormat formatTime = new SimpleDateFormat("HHmm");
                    String mDate = formatDate.format(date);
                    String mTime = formattingForApi(formatTime.format(date));
                    WeatherApi weatherApi = new WeatherApi(mDate, mTime, String.valueOf(getSearchLatitude()), String.valueOf(getSearchLongitude()));

                    //미세먼지, 오존
                    DustApi dustApi = new DustApi(getSearchAddress());
                    Bundle bundle = new Bundle();
                    bundle.putString("dust", dustApi.getDust());
                    bundle.putString("temp", weatherApi.getTemp());
                    bundle.putString("humid", weatherApi.getHumid());
                    bundle.putString("o3", dustApi.getO3());
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }.start();
        } else {
            System.out.println("스레드 X");
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String dust = bundle.getString("dust");
            String temp = bundle.getString("temp");
            String humid = bundle.getString("humid");
            String o3 = bundle.getString("o3");

            System.out.println("dust : " + temp);
            dustText.setText(dust);
            tempText.setText(temp);
            humidText.setText(humid);
            o3Text.setText(o3);

            holdEnvironmentDataPut(dust, temp, humid, o3);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null && data.getData() != null) {
            uri = data.getData();
            if (uri != null) {
                filePath = FetchPath.getPath(getContext(), uri);
                File file = new File(filePath);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                dateText.setText(format1.format(file.lastModified()));
                timeText.setText(format2.format(file.lastModified()));
            }
            setImage(uri);
            dialog.dialog.dismiss();
            holdEnvironmentDataGet();
        }

        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            galleryAddPhoto();
            filePath = photoCurrentPath;

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.getDefault());

            dateText.setText(format1.format(date));
            timeText.setText(format2.format(date));

            photoLayout.setBackgroundColor(Color.parseColor("#fafafa"));
            photoAddText.setVisibility(View.INVISIBLE);
            plusImg.setVisibility(View.INVISIBLE);

            setImage(imgUri);
            dialog.dialog.dismiss();
            holdEnvironmentDataGet();
        }
    }

    private void makeDialog() {
        dialog.callFunction();
        dialog.camera_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        dialog.photo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(photoIntent, REQUEST_CODE);
            }
        });

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file != null) {
                Uri providerUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), file);
                imgUri = providerUri;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, providerUri);

                startActivityForResult(intent, FROM_CAMERA);
            }
        }

    }

    private File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imgFileName);
        photoCurrentPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void galleryAddPhoto() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photoCurrentPath);
        if (file.exists()) {
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            getContext().sendBroadcast(mediaScanIntent);
        }
    }

    private void setImage(Uri uri) {
        try {
            InputStream in = getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(bitmap);

            //이미지 불러오기가 완료됐으므로 기존의 위젯들 보여주지 않기
            photoLayout.setBackgroundColor(Color.parseColor("#fafafa"));
            photoAddText.setVisibility(View.INVISIBLE);
            plusImg.setVisibility(View.INVISIBLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void setPermission() {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "카메라 요청 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(getContext())
                .setPermissionListener(listener)
                .setRationaleMessage("카메라 앱을 사용하시려면 권한을 허용해주세요.")
                .setDeniedMessage("권한을 거부하셨습니다. 앱을 사용하시려면 [앱 설정] -> [권한] 항목에서 권한을 허용해주세요.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void connectToServer() {
        RetrofitAPI retrofitAPI = ServiceGeneratorWithImg.createService(RetrofitAPI.class, getAccessToken());

        File file = new File(filePath);

        RequestBody resBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("picture", filePath, resBody);

        Map<String, RequestBody> map = new HashMap<>();

        map.put("location", RequestBody.create(locateText.getText().toString(), MediaType.parse("text/plain")));
        map.put("date", RequestBody.create(dateText.getText().toString(), MediaType.parse("text/plain")));
        map.put("time", RequestBody.create(timeText.getText().toString(), MediaType.parse("text/plain")));
        map.put("tags", RequestBody.create(getTagState(), MediaType.parse("text/plain")));
        map.put("description", RequestBody.create(descEdit.getText().toString(), MediaType.parse("text/plain")));
        map.put("temp", RequestBody.create(tempText.getText().toString(), MediaType.parse("text/plain")));
        map.put("humid", RequestBody.create(humidText.getText().toString(), MediaType.parse("text/plain")));
        map.put("dust", RequestBody.create(dustText.getText().toString(), MediaType.parse("text/plain")));
        map.put("atm", RequestBody.create(o3Text.getText().toString(), MediaType.parse("text/plain")));

        retrofitAPI.postAddData(map, uploadFile).enqueue(new Callback<AddDataPost>() {
            @Override
            public void onResponse(@NotNull Call<AddDataPost> call, @NotNull Response<AddDataPost> response) {
                AddDataPost block = response.body();
                if (response.isSuccessful()) {
                    Log.d("Server/AddFragment", block.getMsg());
                    Log.d("Server/AddFragment", block.getStatus());

                    getActivity().onBackPressed();
                } else {
                    ResponseBody err = response.errorBody();
                    try {
                        Log.d("Server/AddFragment", err.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<AddDataPost> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("날짜 및 시간 입력 방법").setMessage("\n사진을 등록하면 자동으로 사진을 찍은 날짜와 시간이 입력됩니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getSearchString() {
        String str;
        sharedPref = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        str = sharedPref.getString("search_data", "");

        return str;
    }

    private void holdEnvironmentDataPut(String dust, String temp, String humid, String o3) {
        sharedPref = getActivity().getSharedPreferences("envData", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString("sharedDust", dust);
        editor.putString("sharedTemp", temp);
        editor.putString("sharedHumid", humid);
        editor.putString("sharedO3", o3);

        editor.apply();
    }

    private void holdEnvironmentDataGet() {
        sharedPref = getActivity().getSharedPreferences("envData", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        dustText.setText(sharedPref.getString("sharedDust", ""));
        tempText.setText(sharedPref.getString("sharedTemp", ""));
        humidText.setText(sharedPref.getString("sharedHumid", ""));
        o3Text.setText(sharedPref.getString("sharedO3", ""));
    }

    private String getSearchAddress() {
        String str;
        sharedPref = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        str = sharedPref.getString("search_ad", "");
        return str;
    }

    private int getSearchLatitude() {
        double data;
        sharedPref = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        data = sharedPref.getFloat("search_latitude", 1);
        return (int) data;
    }

    private int getSearchLongitude() {
        double data;
        sharedPref = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        data = sharedPref.getFloat("search_longitude", 1);
        return (int) data;
    }

    private String getTagState() {
        ArrayList<String> mCheckBoxIdx = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (airCheckBox.isChecked()) {
                mCheckBoxIdx.add("\"" + airCheckBox.getText().toString() + "\"");
                airCheckBox.setChecked(false);
            }
            if (waterCheckBox.isChecked()) {
                mCheckBoxIdx.add("\"" + waterCheckBox.getText().toString() + "\"");
                waterCheckBox.setChecked(false);
            }
            if (soilCheckBox.isChecked()) {
                mCheckBoxIdx.add("\"" + soilCheckBox.getText().toString() + "\"");
                soilCheckBox.setChecked(false);
            }
            if (roadCheckBox.isChecked()) {
                mCheckBoxIdx.add("\"" + roadCheckBox.getText().toString() + "\"");
                roadCheckBox.setChecked(false);
            }
            if (etcCheckBox.isChecked()) {
                mCheckBoxIdx.add("\"" + etcCheckBox.getText().toString() + "\"");
                etcCheckBox.setChecked(false);
            }
        }
        return mCheckBoxIdx.toString();
    }

    private void initSharedSearch() {
        sharedPref = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    private void initSharedEnvData() {
        sharedPref = getActivity().getSharedPreferences("envData", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    private String formattingForApi(String str) {
        double time = Double.parseDouble(str);
        //0200 -> 200.0 으로 변환
        if (time >= 200.0 && time <= 459.0) {
            return "0200";
        } else if (time >= 500.0 && time <= 759.0) {
            return "0500";
        } else if (time >= 800.0 && time <= 1059.0) {
            return "0800";
        } else if (time >= 1100.0 && time <= 1359.0) {
            return "1100";
        } else if (time >= 1400.0 && time <= 1659.0) {
            return "1400";
        } else if (time >= 1700.0 && time <= 1959.0) {
            return "1700";
        } else if (time >= 2000.0 && time <= 2259.0) {
            return "2000";
        } else if ((time >= 2300.0 && time <= 2359.0) || (time >= 0.0 && time <= 159.0)) {
            return "2300";
        }
        return null;
    }

    private String getRealPathFromUri(Uri uri) {
        String path;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);
            cursor.close();
        }

        return path;
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

