package com.daffa.dokter2an;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword;
    private Spinner spinnerRole;
    private Button btnLogin;
    private ProgressBar progressLogin;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final String BASE_URL =
            "https://api.xixixixixi.biz.id/api/v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        );

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnLogin = findViewById(R.id.btnLogin);
        progressLogin = findViewById(R.id.progressLogin);

        inputPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2;

                if (inputPassword.getCompoundDrawables()[drawableEnd] != null
                        && event.getRawX() >= inputPassword.getRight()
                        - inputPassword.getCompoundDrawables()[drawableEnd]
                        .getBounds().width()
                        - inputPassword.getPaddingEnd()) {

                    int cursorPosition =
                            inputPassword.getSelectionStart();

                    if (inputPassword.getTransformationMethod()
                            instanceof PasswordTransformationMethod) {

                        inputPassword.setTransformationMethod(
                                HideReturnsTransformationMethod.getInstance()
                        );

                    } else {

                        inputPassword.setTransformationMethod(
                                PasswordTransformationMethod.getInstance()
                        );
                    }

                    inputPassword.setSelection(
                            Math.max(0, cursorPosition)
                    );

                    return true;
                }
            }

            return false;
        });

        String[] roles = {
                "Pilih Role",
                "Admin",
                "Doctor",
                "Nurse",
                "Patient",
                "Receptionist"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRole.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> prosesLogin());
    }

    private void prosesLogin() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String role = spinnerRole.getSelectedItem().toString();

        if (username.isEmpty()) {
            inputUsername.setError("Username wajib diisi");
            inputUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password wajib diisi");
            inputPassword.requestFocus();
            return;
        }

        if (spinnerRole.getSelectedItemPosition() == 0) {
            Toast.makeText(
                    this,
                    "Pilih role terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        btnLogin.setEnabled(false);
        progressLogin.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            HttpURLConnection connection = null;
            String message;
            boolean success = false;

            try {
                URL url = new URL(BASE_URL + "/token");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                String body =
                        "grant_type=" + URLEncoder.encode(
                                "password", "UTF-8"
                        )
                                + "&username=" + URLEncoder.encode(
                                username, "UTF-8"
                        )
                                + "&password=" + URLEncoder.encode(
                                password, "UTF-8"
                        );

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(body.getBytes(StandardCharsets.UTF_8));
                }

                int status = connection.getResponseCode();

                InputStream stream = status >= 200 && status < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                StringBuilder response = new StringBuilder();

                if (stream != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    stream,
                                    StandardCharsets.UTF_8
                            ))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                }

                if (status >= 200 && status < 300) {
                    JSONObject json =
                            new JSONObject(response.toString());

                    String token = json.optString("access_token", "");

                    if (token.isEmpty()) {
                        message = "Login gagal: token tidak ditemukan";
                    } else {
                        success = true;
                        message = "Login API berhasil sebagai " + role;
                    }

                } else {
                    JSONObject json =
                            new JSONObject(response.toString());

                    message = json.optString(
                            "detail",
                            "Login gagal. HTTP " + status
                    );
                }

            } catch (Exception e) {
                message = "Gagal menghubungi server: "
                        + e.getMessage();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            final String resultMessage = message;
            final boolean loginSuccess = success;

            runOnUiThread(() -> {
                progressLogin.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                Toast.makeText(
                        LoginActivity.this,
                        resultMessage,
                        Toast.LENGTH_LONG
                ).show();

                if (loginSuccess) {
                    inputPassword.setText("");
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
