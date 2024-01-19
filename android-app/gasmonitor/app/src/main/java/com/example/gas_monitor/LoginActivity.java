package com.example.gas_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                attemptLogin(username, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // RegisterActivity
                startActivity(intent);
            }
        });
    }

    private void attemptLogin(String username, String password) {
        // Construct the JSON payload
        JSONObject loginPayload = new JSONObject();
        try {
            loginPayload.put("username", username);
            loginPayload.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON exception
        }

        // Create a new thread for network operations
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    loginPayload.toString()
            );

            Request request = new Request.Builder()
                    .url("http://3.145.132.225:5000/login")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                handler.post(() -> handleLoginResult(result, username));
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void handleLoginResult(String result, String username) {
        if (result != null && result.contains("logged in successfully")) {
            // Login successful, transition to MainActivity
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("USERNAME_KEY", username);
            editor.putBoolean("LOGGED_IN_KEY", true);
            editor.apply();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Login failed, show error message
            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
        }
    }
}
