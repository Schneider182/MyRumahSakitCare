
package com.daffa.dokter2an;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class Doctor_Dashboard_Activity extends AppCompatActivity {

    private SessionActivity session;

    private TextView txtDoctorName;
    private TextView txtDoctorUsername;
    private TextView txtDoctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        session = new SessionActivity(this);

        txtDoctorName = findViewById(R.id.txtDoctorName);
        txtDoctorUsername = findViewById(R.id.txtDoctorUsername);
        txtDoctorId = findViewById(R.id.txtDoctorId);

        MaterialCardView btnProfile = findViewById(R.id.btnProfile);
        MaterialCardView btnAppointments = findViewById(R.id.btnAppointments);
        MaterialCardView btnPatients = findViewById(R.id.btnPatients);
        MaterialCardView btnTreatments = findViewById(R.id.btnTreatments);
        MaterialCardView btnBedAdmissions = findViewById(R.id.btnBedAdmissions);

        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        loadDoctorProfile();

        btnProfile.setOnClickListener(v -> loadDoctorProfile());

        btnAppointments.setOnClickListener(v ->
                showFeatureMessage("Appointments"));

        btnPatients.setOnClickListener(v ->
                showFeatureMessage("Patients"));

        btnTreatments.setOnClickListener(v ->
                showFeatureMessage("Treatments"));

        btnBedAdmissions.setOnClickListener(v ->
                showFeatureMessage("Bed Admissions"));

        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadDoctorProfile() {
        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            logout();
            return;
        }

        ClientActivity.request(
                "/users/me",
                "GET",
                token,
                null,
                new ClientActivity.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            String username = response.optString(
                                    "username",
                                    "Doctor"
                            );

                            String userId = response.optString(
                                    "user_id",
                                    "-"
                            );

                            txtDoctorName.setText("Welcome, " + username);
                            txtDoctorUsername.setText("Username: " + username);
                            txtDoctorId.setText("User ID: " + userId);
                        });
                    }

                    @Override
                    public void onError(String message, int code) {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    Doctor_Dashboard_Activity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            if (code == 401 || code == 403) {
                                logout();
                            }
                        });
                    }
                }
        );
    }

    private void showFeatureMessage(String feature) {
        Toast.makeText(
                this,
                feature + " page will be implemented next",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void logout() {
        session.clearSession();

        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
