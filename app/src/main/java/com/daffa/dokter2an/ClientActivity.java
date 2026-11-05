
package com.daffa.dokter2an;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClientActivity {

    private static final String BASE_URL =
            "https://api.xixixixixi.biz.id/api/v1";

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String message, int statusCode);
    }

    public interface ApiArrayCallback {
        void onSuccess(JSONArray response);
        void onError(String message, int statusCode);
    }

    public static void request(
            String endpoint,
            String method,
            String token,
            JSONObject body,
            ApiCallback callback
    ) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(BASE_URL + endpoint);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(method);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Accept", "application/json");

                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty(
                            "Authorization",
                            "Bearer " + token
                    );
                }

                if (body != null) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty(
                            "Content-Type",
                            "application/json"
                    );

                    try (OutputStream output = connection.getOutputStream()) {
                        output.write(
                                body.toString().getBytes(StandardCharsets.UTF_8)
                        );
                    }
                }

                int statusCode = connection.getResponseCode();

                InputStream stream = statusCode >= 200 && statusCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                StringBuilder response = new StringBuilder();

                if (stream != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                }

                JSONObject json = response.length() > 0
                        ? new JSONObject(response.toString())
                        : new JSONObject();

                if (statusCode >= 200 && statusCode < 300) {
                    callback.onSuccess(json);
                } else {
                    callback.onError(
                            json.optString(
                                    "detail",
                                    "Request gagal. HTTP " + statusCode
                            ),
                            statusCode
                    );
                }

            } catch (Exception e) {
                callback.onError(
                        "Gagal menghubungi server: " + e.getMessage(),
                        -1
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    public static void requestArray(
            String endpoint,
            String method,
            String token,
            ApiArrayCallback callback
    ) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(BASE_URL + endpoint);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(method);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Accept", "application/json");

                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty(
                            "Authorization",
                            "Bearer " + token
                    );
                }

                int statusCode = connection.getResponseCode();

                InputStream stream = statusCode >= 200 && statusCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                StringBuilder response = new StringBuilder();

                if (stream != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                }

                if (statusCode >= 200 && statusCode < 300) {
                    callback.onSuccess(new JSONArray(response.toString()));
                } else {
                    String message = "Request gagal. HTTP " + statusCode;

                    try {
                        JSONObject error = new JSONObject(response.toString());
                        message = error.optString("detail", message);
                    } catch (Exception ignored) {
                    }

                    callback.onError(message, statusCode);
                }

            } catch (Exception e) {
                callback.onError(
                        "Gagal menghubungi server: " + e.getMessage(),
                        -1
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
