package com.example.gas_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {

    //constant variables, flags, and other
    private final String serverUrl = "192.168.0.215:5000";
    private boolean isUserLoggedOut = false;
    private String username;
    private  String dangerLevel = "";
    private final long REFRESH_INTERVAL = 5000; // 2 seconds

    //UI elements
    private Spinner deviceSpinner;
    private TextView dangervalueView;
    private ArrayList<String> deviceList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private ArcGauge ppmGauge;
    private ArcGauge tempGauge;
    private ArcGauge humGauge;
    private HalfGauge dangerGauge;
    com.ekn.gruzer.gaugelibrary.Range range1, range2, range3, danger1, danger2, danger3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;

        range1 = new com.ekn.gruzer.gaugelibrary.Range();
        range2 = new com.ekn.gruzer.gaugelibrary.Range();
        range3 = new com.ekn.gruzer.gaugelibrary.Range();
        range1.setFrom(0);range1.setTo(250);range1.setColor(getResources().getColor(R.color.green));
        range2.setFrom(250);range2.setTo(500);range2.setColor(getResources().getColor(R.color.orange));
        range3.setFrom(500);range3.setTo(1000);range3.setColor(getResources().getColor(R.color.red));

        danger1 = new com.ekn.gruzer.gaugelibrary.Range();
        danger2 = new com.ekn.gruzer.gaugelibrary.Range();
        danger3 = new com.ekn.gruzer.gaugelibrary.Range();
        danger1.setFrom(0);danger1.setTo(2);danger1.setColor(getResources().getColor(R.color.green));
        danger2.setFrom(2);danger2.setTo(4);danger2.setColor(getResources().getColor(R.color.orange));
        danger3.setFrom(4);danger3.setTo(5);danger3.setColor(getResources().getColor(R.color.red));


        ppmGauge = findViewById(R.id.ppmGauge);
        tempGauge = findViewById(R.id.tempGauge);
        humGauge = findViewById(R.id.humGauge);
        dangerGauge = findViewById(R.id.dangerGauge);

        ppmGauge.setMinValue(0);ppmGauge.setMaxValue(1000);ppmGauge.setValue(0);ppmGauge.addRange(range1);ppmGauge.addRange(range2);ppmGauge.addRange(range3);
        tempGauge.setMinValue(0);tempGauge.setMaxValue(100);tempGauge.setValue(0);tempGauge.addRange(range1);tempGauge.addRange(range2);tempGauge.addRange(range3);
        humGauge.setMinValue(0);humGauge.setMaxValue(100);humGauge.setValue(0);humGauge.addRange(range1);humGauge.addRange(range2);humGauge.addRange(range3);
        dangerGauge.setMinValue(0);dangerGauge.setMaxValue(5);dangerGauge.setValue(0);dangerGauge.addRange(danger1);dangerGauge.addRange(danger2);dangerGauge.addRange(danger3);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN_KEY", false);
        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            username = sharedPreferences.getString("USERNAME_KEY", "");

        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Optionally refresh MainActivity or do nothing if it's already displaying
            } else if (itemId == R.id.navigation_logout) {
                logoutUser();
            }
            return true;
        });

        deviceSpinner = findViewById(R.id.deviceSpinner);
        dangervalueView = findViewById(R.id.dangervalueview);
        startPPMMonitorService();
    }
    @Override
    protected void onStart() {
        super.onStart();
        fetchDeviceList();
        startAutoRefresh();

    }

    private void fetchDeviceList() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            try {
                json.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url("http://" + serverUrl +"/retrieve_devices")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray jsonArray = jsonResponse.getJSONArray("devices");
                    deviceList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        deviceList.add(jsonArray.getString(i));
                    }
                    handler.post(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, deviceList);
                        deviceSpinner.setAdapter(adapter);
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void fetchDeviceData(String deviceId) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            try {
                json.put("device_uid", deviceId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url("http://"+serverUrl +"/read_monitor/")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);

                    ppmGauge.setValue(Float.parseFloat(jsonResponse.getString("ppm")));
                    humGauge.setValue(Float.parseFloat(jsonResponse.getString("hum")));
                    tempGauge.setValue(Float.parseFloat(jsonResponse.getString("temp")));
                    dangervalueView.setText(jsonResponse.getString("danger"));
                    dangerLevel = jsonResponse.getString("danger");
                    if(jsonResponse.getString("danger").equals("Low")) {
                        dangerGauge.setValue(0);
                    }else if(jsonResponse.getString("danger").equals("Med")) {
                        dangerGauge.setValue(2);
                    }else if(jsonResponse.getString("danger").equals("High")) {
                        dangerGauge.setValue(5);
                    }
                    Log.d("Danger Level", dangerLevel);
                    updatePPMValue();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void startPPMMonitorService() {
        Intent serviceIntent = new Intent(this, PPMMonitorService.class);
        // Example of passing a variable. Replace with actual data.
        serviceIntent.putExtra("dangerLevel", dangerLevel); // Replace with actual key and value
        startService(serviceIntent);
    }
    private void startAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!deviceList.isEmpty()) {
                    String selectedDevice = deviceSpinner.getSelectedItem().toString();
                    fetchDeviceData(selectedDevice);
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            }
        }, REFRESH_INTERVAL);
    }
    private void updatePPMValue() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("UPDATE_DANGER_LEVEL");
        broadcastIntent.setPackage(getPackageName());
        if (isUserLoggedOut) {
            broadcastIntent.putExtra("dangerLevel", "Low");
        } else {
        broadcastIntent.putExtra("dangerLevel", dangerLevel);
    }
        Log.d("Danger Level", dangerLevel);
        sendBroadcast(broadcastIntent);
    }
    private void logoutUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("LOGGED_IN_KEY");
        editor.remove("USERNAME_KEY");
        editor.apply();
        isUserLoggedOut = true;
        handler.removeCallbacksAndMessages(null);
        Intent serviceIntent = new Intent(this, PPMMonitorService.class);
        stopService(serviceIntent);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

}