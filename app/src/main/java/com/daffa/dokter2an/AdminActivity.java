package com.daffa.dokter2an;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

public class AdminActivity extends AppCompatActivity {

    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        session = new SessionActivity(this);

        MaterialCardView btnPatients = findViewById(R.id.btnPatients);
        MaterialCardView btnDoctors = findViewById(R.id.btnDoctors);
        MaterialCardView btnStaff = findViewById(R.id.btnStaff);
        MaterialCardView btnBeds = findViewById(R.id.btnBeds);
        MaterialCardView btnBilling = findViewById(R.id.btnBilling);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        btnPatients.setOnClickListener(v ->
                startActivity(new Intent(this, PatientActivity.class)));

        btnDoctors.setOnClickListener(v ->
                startActivity(new Intent(this, DoctorActivity.class)));

        btnPatients.setOnClickListener(v ->
                startActivity(new Intent(this, PatientActivity.class)));

        btnDoctors.setOnClickListener(v ->
                startActivity(new Intent(this, DoctorActivity.class)));

        btnStaff.setOnClickListener(v ->
                startActivity(new Intent(this, StaffActivity.class)));

        btnBeds.setOnClickListener(v ->
                startActivity(new Intent(this, BedActivity.class)));

        btnBilling.setOnClickListener(v ->
                startActivity(new Intent(this, BillingActivity.class)));

        btnLogout.setOnClickListener(v -> logout());
    }

    private void showFeatureMessage(String feature) {
        Toast.makeText(
                this,
                feature + " belum diimplementasikan",
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