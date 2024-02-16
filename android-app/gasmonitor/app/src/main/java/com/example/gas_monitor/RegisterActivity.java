package com.example.gas_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private final String serverUrl = "192.168.0.243:5000";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private Button signupButton;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);

        signupButton = (Button) findViewById(R.id.signup);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                attemptSignup(username, password, name, email);
            }
        });
    }

    private void attemptSignup(String username, String password, String name, String email) {
        JSONObject signupPayload = new JSONObject();
        try {
            signupPayload.put("username", username);
            signupPayload.put("password", password);
            signupPayload.put("name", name);
            signupPayload.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            String url = "http://"+ serverUrl+"/signup";
            okhttp3.MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, signupPayload.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String responseString = response.body().string();
                handler.post(() -> handleRegisterResult(responseString));

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(RegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void handleRegisterResult(String result) {
        if (result != null && result.contains("registered successfully")){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}