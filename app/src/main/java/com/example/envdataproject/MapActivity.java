package com.example.envdataproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageButton backBtn;
    ProgressDialog progressDialog;
    AutocompleteSupportFragment autocompleteFragment;

    private GoogleMap mMap;
    PlacesClient placesClient;

    String chartFragmentStr, addFragmentStr;

    private SharedPreferences sharedPref = null;
    private SharedPreferences.Editor editor = null;

    private static final String API_KEY = "AIzaSyDQLtA5j9-pGZ5VoI_eTcUGGCBTzbC_V8g";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        addFragmentStr = intent.getStringExtra("AddFragment");
        chartFragmentStr = intent.getStringExtra("ChartFragment");

        backBtn = findViewById(R.id.map_back_btn);

        Places.initialize(this, API_KEY, Locale.KOREA);
        placesClient = Places.createClient(this);

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.map_search);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(37.45639, 126.70528),
                new LatLng(37.56667, 126.97806)));
        autocompleteFragment.setCountries("KR");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!checkLocationServiceStatus()) {
            showDialogForLocation();
        } else {
            checkRunTimePermission();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoulTech = new LatLng(37.631668399999995, 127.07748129999999);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulTech, 14));

        //GPS 관련 권한
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    activateGPS(mMap);
                }
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.clear();

                String placeName = place.getName(); // place -> 지도 기반으로 가져올 수 있는 데이터들
                String placeAddress = place.getAddress().replace("대한민국 ", "");
                LatLng placePoint = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.title(placeName);
                markerOptions2.snippet(placeAddress);
                markerOptions2.position(placePoint);
                mMap.addMarker(markerOptions2);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placePoint, 15));
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if ("add".equals(addFragmentStr)) {
                            sharedForAddFragment(place.getName(), placeAddress, place.getLatLng().latitude, place.getLatLng().longitude);
                        } else if ("chart".equals(chartFragmentStr)) {
                            sharedForChartFragment(place.getName());
                        } else {
                            completeSearch(place.getName());
                        }
                    }
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                System.out.println(status);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {
            if (checkLocationServiceStatus()) {
                Toast.makeText(this, "GPS 활성화 완료", Toast.LENGTH_SHORT).show();
                checkRunTimePermission();
            }
        }
    }

    private void activateGPS(GoogleMap googleMap) {
        mMap = googleMap;
        List<Place.Field> placeField = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeField);
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("현재 위치 검색 중..");
        progressDialog.setMessage("검색을 바로 원하신다면 아무 화면을\n누르십시오.");
        progressDialog.show();

        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();

                progressDialog.setOnDismissListener(dialogInterface -> {
                    dialogInterface.dismiss();
                });

                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    String name = placeLikelihood.getPlace().getName();
                    String address = placeLikelihood.getPlace().getAddress().replace("대한민국 ", "");
                    double latitude = placeLikelihood.getPlace().getLatLng().latitude;
                    double longitude = placeLikelihood.getPlace().getLatLng().longitude;
                    LatLng point = new LatLng(latitude, longitude);

                    MarkerOptions markerOptions2 = new MarkerOptions();
                    markerOptions2.title(name);
                    markerOptions2.snippet(address);
                    markerOptions2.position(point);

                    if (response.getPlaceLikelihoods().get(0).getPlace().getName().equals(placeLikelihood.getPlace().getName())) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                    } else {
                        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_place));
                    }
                    mMap.addMarker(markerOptions2);
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if ("add".equals(addFragmentStr)) {
                            sharedForAddFragment(marker.getTitle(), marker.getSnippet(), marker.getPosition().latitude, marker.getPosition().longitude);
                        } else if ("chart".equals(chartFragmentStr)) {
                            sharedForChartFragment(marker.getTitle());
                        } else {
                            completeSearch(marker.getTitle());
                        }
                    }
                });
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("Error", "Place Not Found : " + apiException.getStatusCode());
                    Toast.makeText(this, "현재 위치를 찾는 데 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                }
            }
            progressDialog.dismiss();
        });
    }

    //openApi 에게 데이터 주기 위한 메소드 with sharedPreferences
    private void sharedForAddFragment(String name, String addressName, double latitude, double longitude) {
        String locationName = getLocateName(addressName.substring(0, addressName.indexOf(" ")));
        sharedPref = getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString("search_data", name);
        editor.putString("search_ad", locationName);
        editor.putFloat("search_latitude", (float) latitude);
        editor.putFloat("search_longitude", (float) longitude);
        editor.apply();
        onBackPressed();
    }

    private void sharedForChartFragment(String name) {
        sharedPref = getSharedPreferences("search", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString("search_data1", name);
        editor.apply();
        onBackPressed();
    }

    private void completeSearch(String name) {
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        intent.putExtra("searchStr", name);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void checkRunTimePermission() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!(fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(this, "현재 위치를 설정하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 권한").setMessage("현재 위치를 설정하기 위해서 위치 서비스가 필요합니다.");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGpsIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.create().show();
    }

    private boolean checkLocationServiceStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private String getLocateName(String str) {
        if (str != null) {
            String returnStr = "";
            switch (str) {
                case "서울특별시":
                    returnStr = "서울";
                    break;
                case "부산광역시":
                    returnStr = "부산";
                    break;
                case "대구광역시":
                    returnStr = "대구";
                    break;
                case "인천광역시":
                    returnStr = "인천";
                    break;
                case "광주광역시":
                    returnStr = "광주";
                    break;
                case "대전광역시":
                    returnStr = "대전";
                    break;
                case "울산광역시":
                    returnStr = "울산";
                    break;
                case "경기도":
                    returnStr = "경기";
                    break;
                case "강원도":
                    returnStr = "강원";
                    break;
                case "충청북도":
                    returnStr = "충북";
                    break;
                case "충청남도":
                    returnStr = "충남";
                    break;
                case "전라북도":
                    returnStr = "전북";
                    break;
                case "전라남도":
                    returnStr = "전남";
                    break;
                case "경상북도":
                    returnStr = "경북";
                    break;
                case "경상남도":
                    returnStr = "경남";
                    break;
                case "제주특별자치도":
                    returnStr = "제주";
                    break;
                case "세종특별자치시":
                case "세종":
                    returnStr = "세종";
                    break;
            }
            return returnStr;
        } else {
            return null;
        }
    }
}
