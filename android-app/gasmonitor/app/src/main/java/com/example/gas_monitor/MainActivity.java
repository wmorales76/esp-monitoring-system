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
    private final String serverUrl = "18.220.56.59:5000";
    private boolean isUserLoggedOut = false;
    private Spinner deviceSpinner;
    private TextView dangervalueView;
    private ArrayList<String> deviceList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private String username;
    private  String dangerLevel = "";
    private final long REFRESH_INTERVAL = 5000; // 2 seconds

    ArcGauge ppmGauge;
    ArcGauge tempGauge;
    ArcGauge humGauge;
    HalfGauge dangerGauge;
    com.ekn.gruzer.gaugelibrary.Range range1, range2, range3;
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

        ppmGauge = findViewById(R.id.ppmGauge);
        tempGauge = findViewById(R.id.tempGauge);
        humGauge = findViewById(R.id.humGauge);
        dangerGauge = findViewById(R.id.dangerGauge);

        ppmGauge.setMinValue(0);ppmGauge.setMaxValue(1000);ppmGauge.setValue(0);
        tempGauge.setMinValue(0);tempGauge.setMaxValue(100);tempGauge.setValue(0);
        humGauge.setMinValue(0);humGauge.setMaxValue(100);humGauge.setValue(0);
        dangerGauge.setMinValue(0);dangerGauge.setMaxValue(5);dangerGauge.setValue(0);

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
            } else if (itemId == R.id.navigation_history) {
                startHistoryActivity();
            } else if (itemId == R.id.navigation_logout) {
                logoutUser();
            }
            return true;
        });

        deviceSpinner = findViewById(R.id.deviceSpinner);
        dangervalueView = findViewById(R.id.dangervalueview);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchDeviceData(deviceList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        startPPMMonitorService();
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchDeviceList();
    }
    private void startHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
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
                        if (!deviceList.isEmpty()) {
                            fetchDeviceData(deviceList.get(0));
                            startAutoRefresh();
                        }
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
                    dangervalueView.setText(jsonResponse.getString("danger"));
                    dangerLevel = jsonResponse.getString("danger");
                    if(jsonResponse.getString("danger").equals("Low")) {
                        dangerGauge.setValue(0);
                    }else if(jsonResponse.getString("danger").equals("Medium")) {
                        dangerGauge.setValue(2);
                    }else if(jsonResponse.getString("danger").equals("High")) {
                        dangerGauge.setValue(5);
                    }

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
        if(isUserLoggedOut){
            broadcastIntent.putExtra("dangerLevel", "Low");
        }
        broadcastIntent.putExtra("dangerLevel", dangerLevel);
        sendBroadcast(broadcastIntent);
    }


}