package com.tbl324.mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        float d = getResources().getDisplayMetrics().density;
        int padH = (int) (24 * d);
        int fieldH = (int) (52 * d);
        int marginV = (int) (12 * d);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // Blue header block
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(0xFF1565C0);
        header.setPadding(padH, (int) (60 * d), padH, (int) (32 * d));

        TextView appName = new TextView(this);
        appName.setText("TBL324 Ticketing");
        appName.setTextSize(26);
        appName.setTextColor(Color.WHITE);
        appName.setTypeface(null, Typeface.BOLD);
        header.addView(appName);

        TextView subtitle = new TextView(this);
        subtitle.setText("Sisteme giriş yapın");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xCCFFFFFF);
        LinearLayout.LayoutParams subP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subP.topMargin = (int) (4 * d);
        header.addView(subtitle, subP);

        root.addView(header);

        // Form
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(padH, (int) (32 * d), padH, padH);

        EditText usernameField = new EditText(this);
        usernameField.setHint("Kullanıcı adı");
        usernameField.setTextSize(16);
        LinearLayout.LayoutParams up = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, fieldH);
        up.bottomMargin = marginV;
        form.addView(usernameField, up);

        EditText passwordField = new EditText(this);
        passwordField.setHint("Şifre");
        passwordField.setTextSize(16);
        passwordField.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, fieldH);
        pp.bottomMargin = (int) (24 * d);
        form.addView(passwordField, pp);

        Button loginBtn = new Button(this);
        loginBtn.setText("GİRİŞ YAP");
        loginBtn.setTextColor(Color.WHITE);
        loginBtn.setBackgroundColor(0xFF1565C0);
        loginBtn.setTextSize(16);
        loginBtn.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }
            login(username, password);
        });
        form.addView(loginBtn, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        root.addView(form);
        setContentView(root);
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
                        String userIdStr = response.body().get("userId");
                        if (userIdStr != null) {
                            try {
                                ApiClient.getInstance().setUserId(Double.valueOf(userIdStr).longValue());
                            } catch (NumberFormatException ignored) {}
                        }
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
