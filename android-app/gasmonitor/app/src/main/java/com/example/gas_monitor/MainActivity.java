package com.example.gas_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class MainActivity extends AppCompatActivity {

    private Spinner deviceSpinner;
    private TextView dataTextView;
    private ArrayList<String> deviceList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private String username;
    private final long REFRESH_INTERVAL = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME_KEY");

        deviceSpinner = findViewById(R.id.deviceSpinner);
        dataTextView = findViewById(R.id.dataTextView);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchDeviceData(deviceList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fetchDeviceList();
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
                    .url("http://192.168.0.243:5000/retrieve_devices")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
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
                json.put("device_id", deviceId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url("http://192.168.0.243:5000/read_monitor/")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    String displayText = "PPM: " + jsonResponse.getString("ppm") +
                            "\nTemp: " + jsonResponse.getString("temp") +
                            "\nHumidity: " + jsonResponse.getString("hum") +
                            "\nDanger: " + jsonResponse.getString("danger");
                    handler.post(() -> dataTextView.setText(displayText));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
