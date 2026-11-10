package com.daffa.dokter2an;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private Button btnLogin;
    private ProgressBar progressLogin;

    private SessionActivity session;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final String BASE_URL =
            "https://api.xixixixixi.biz.id/api/v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        );

        window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        );

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        session = new SessionActivity(this);

        String token = session.getToken();

        if (token != null && !token.isEmpty()) {
            setContentView(R.layout.activity_splash);
            verifikasiUser(token, false);
        } else {
            setupLoginScreen();
        }
    }

    private void setupLoginScreen() {
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
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

                    int cursorPosition = inputPassword.getSelectionStart();

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

        btnLogin.setOnClickListener(v -> prosesLogin());
    }

    private void showLoginForm() {
        if (inputUsername == null) {
            setupLoginScreen();
        }

        inputUsername.setVisibility(View.VISIBLE);
        inputPassword.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);

        setLoading(false);
    }

    private void prosesLogin() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString();

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

        setLoading(true);

        executor.execute(() -> {
            HttpURLConnection connection = null;

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
                                "password",
                                "UTF-8"
                        )
                                + "&username=" + URLEncoder.encode(
                                username,
                                "UTF-8"
                        )
                                + "&password=" + URLEncoder.encode(
                                password,
                                "UTF-8"
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
                    JSONObject json = new JSONObject(response.toString());

                    String token = json.optString("access_token", "");

                    if (token.isEmpty()) {
                        runOnUiThread(() -> {
                            setLoading(false);
                            showMessage("Token tidak ditemukan");
                        });
                    } else {
                        runOnUiThread(() ->
                                verifikasiUser(token, true)
                        );
                    }

                } else {
                    JSONObject json = new JSONObject(response.toString());

                    String message = json.optString(
                            "detail",
                            "Login gagal. HTTP " + status
                    );

                    runOnUiThread(() -> {
                        setLoading(false);
                        showMessage(message);
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showMessage("Gagal menghubungi server: " + e.getMessage());
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void verifikasiUser(String token, boolean saveToken) {
        ClientActivity.request(
                "/users/me",
                "GET",
                token,
                null,
                new ClientActivity.ApiCallback() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        String username = response.optString(
                                "username",
                                ""
                        );

                        String actualRole = getRole(username);

                        runOnUiThread(() -> {
                            if (actualRole == null) {
                                session.clearSession();
                                showLoginForm();
                                showMessage("Role akun tidak dikenali");
                                return;
                            }

                            if (saveToken) {
                                session.saveToken(token);
                            }

                            bukaDashboard(actualRole);
                        });
                    }

                    @Override
                    public void onError(String message, int statusCode) {
                        runOnUiThread(() -> {
                            if (statusCode == 401 || statusCode == 403) {
                                session.clearSession();

                                showLoginForm();
                                showMessage(
                                        "Sesi tidak valid atau sudah kedaluwarsa. Silakan login kembali."
                                );
                            } else {
                                showLoginForm();
                                showMessage(message);
                            }
                        });
                    }
                }
        );
    }

    private String getRole(String username) {
        switch (username.toLowerCase()) {
            case "admin":
                return "Admin";

            case "doctor":
                return "Doctor";

            case "nurse":
                return "Nurse";

            case "receptionist":
                return "Receptionist";

            default:
                return null;
        }
    }

    private void bukaDashboard(String role) {
        try {
            Class<?> dashboardClass;

            switch (role) {
                case "Admin":
                    dashboardClass = Class.forName(
                            getPackageName() + ".AdminActivity"
                    );
                    break;

                case "Doctor":
                    dashboardClass = Class.forName(
                            getPackageName() + ".Doctor_Dashboard_Activity"
                    );
                    break;

                case "Nurse":
                    dashboardClass = Class.forName(
                            getPackageName() + ".NurseActivity"
                    );
                    break;

                case "Receptionist":
                    dashboardClass = Class.forName(
                            getPackageName() + ".ReceptionistActivity"
                    );
                    break;

                default:
                    showLoginForm();
                    showMessage("Dashboard tidak ditemukan");
                    return;
            }

            Intent intent = new Intent(this, dashboardClass);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

        } catch (ClassNotFoundException e) {
            showLoginForm();
            showMessage("Dashboard " + role + " belum dibuat");
        }
    }

    private void setLoading(boolean loading) {
        if (btnLogin != null) {
            btnLogin.setEnabled(!loading);
        }

        if (progressLogin != null) {
            progressLogin.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );
        }
    }

    private void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}