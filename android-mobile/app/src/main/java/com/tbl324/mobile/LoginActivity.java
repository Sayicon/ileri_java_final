package com.tbl324.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbl324.mobile.api.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 120, 48, 48);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 24, 0, 24);

        EditText usernameField = new EditText(this);
        usernameField.setHint("Kullanıcı adı");
        layout.addView(usernameField, params);

        EditText passwordField = new EditText(this);
        passwordField.setHint("Şifre");
        passwordField.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordField, params);

        Button loginBtn = new Button(this);
        loginBtn.setText("Giriş Yap");
        loginBtn.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }
            login(username, password);
        });
        layout.addView(loginBtn);

        setContentView(layout);
    }

    private void login(String username, String password) {
        Map<String, String> creds = new HashMap<>();
        creds.put("username", username);
        creds.put("password", password);

        ApiClient.getInstance().getService().login(creds).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().get("token");
                    if (token != null) {
                        ApiClient.getInstance().setToken(token);
                        startActivity(new Intent(LoginActivity.this, EventListActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
