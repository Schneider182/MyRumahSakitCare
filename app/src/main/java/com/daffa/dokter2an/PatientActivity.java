package com.daffa.dokter2an;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PatientActivity extends AppCompatActivity {

    private SessionActivity session;
    private RecyclerView recyclerPatients;
    private ProgressBar progressPatients;
    private TextView txtEmpty;
    private TextView txtPatientCount;
    private Button btnAddPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        session = new SessionActivity(this);

        recyclerPatients = findViewById(R.id.recyclerPatients);
        progressPatients = findViewById(R.id.progressPatients);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtPatientCount = findViewById(R.id.txtPatientCount);
        btnAddPatient = findViewById(R.id.btnAddPatient);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        recyclerPatients.setLayoutManager(new LinearLayoutManager(this));

        btnAddPatient.setOnClickListener(v ->
                startActivity(new Intent(this, AddPatientActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatients();
    }

    private void loadPatients() {
        progressPatients.setVisibility(View.VISIBLE);
        recyclerPatients.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);

        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            progressPatients.setVisibility(View.GONE);

            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        ClientActivity.requestArray(
                "/patients",
                "GET",
                token,
                new ClientActivity.ApiArrayCallback() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        runOnUiThread(() -> {
                            progressPatients.setVisibility(View.GONE);

                            txtPatientCount.setText(
                                    "Patient List (" + response.length() + ")"
                            );

                            if (response.length() == 0) {
                                txtEmpty.setVisibility(View.VISIBLE);
                                recyclerPatients.setVisibility(View.GONE);
                            } else {
                                recyclerPatients.setAdapter(
                                        new PatientAdapter(PatientActivity.this, response)
                                );

                                recyclerPatients.setVisibility(View.VISIBLE);
                                txtEmpty.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(String message, int statusCode) {
                        runOnUiThread(() -> {
                            progressPatients.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.VISIBLE);
                            txtEmpty.setText(message);

                            Toast.makeText(
                                    PatientActivity.this,
                                    "Gagal memuat pasien",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }
                }
        );
    }

    public void confirmDeletePatient(String patientId, String patientName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Patient")
                .setMessage("Yakin ingin menghapus data pasien " + patientName + "?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) ->
                        deletePatient(patientId)
                )
                .show();
    }

    private void deletePatient(String patientId) {
        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        Toast.makeText(
                this,
                "Menghapus pasien...",
                Toast.LENGTH_SHORT
        ).show();

        String endpoint = "/patients/" + Uri.encode(patientId);

        ClientActivity.request(
                endpoint,
                "DELETE",
                token,
                null,
                new ClientActivity.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    PatientActivity.this,
                                    "Patient berhasil dihapus",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadPatients();
                        });
                    }

                    @Override
                    public void onError(String message, int statusCode) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        PatientActivity.this,
                                        "Gagal menghapus pasien: " + message,
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                }
        );
    }
}