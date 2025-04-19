package com.example.android_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_client.api.ApiClient;
import com.example.android_client.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind the activity layout
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(v -> onLoginAttempt());
        Log.d("Login", "Start Activity");
    }

    private void onLoginAttempt() {
        Log.d("Login", "Attempt");
        String username = binding.user.getText().toString();
        String password = binding.password.getText().toString();
        Log.d("Login", "Username: " + username + " Password: " + password);
        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                ApiClient.authenticate((response -> {
                    binding.button.setEnabled(false);
                    String token = response.get("access_token");
                    if (token != null) {
                        ApiClient.token = "Bearer " + token;
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", ApiClient.token);
                        editor.apply();
                        binding.button.setEnabled(true);
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Credenciais Invalidas", Toast.LENGTH_LONG).show();
                    }

                }), username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_LONG).show();
        }
    }
}
