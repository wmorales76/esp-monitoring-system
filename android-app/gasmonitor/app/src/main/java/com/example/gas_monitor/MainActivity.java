package com.example.gas_monitor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final long UPDATE_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        startDataUpdates();
    }

    private void startDataUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new FetchDataFromServer().execute();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private class FetchDataFromServer extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String apiURL = "http://192.168.0.243:5000/device_monitor/Device001";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiURL).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jsonObject = new JSONObject(result);
                    String ppm = jsonObject.getString("ppm");
                    String temperature = jsonObject.getString("temp");
                    String humidity = jsonObject.getString("hum");
                    String dangerLevel = jsonObject.getString("danger");

                    String resultText = "PPM: " + ppm + "\n" +
                            "Temperature: " + temperature + "\n" +
                            "Humidity: " + humidity + "\n" +
                            "Danger Level: " + dangerLevel;
                    resultTextView.setText(resultText);
                } else {
                    resultTextView.setText("No data received from server");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Remove any pending callbacks to avoid memory leaks
    }
}
