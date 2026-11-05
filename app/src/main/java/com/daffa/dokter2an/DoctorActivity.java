
package com.daffa.dokter2an;

import android.content.Intent;
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

public class DoctorActivity extends AppCompatActivity {

    private RecyclerView recyclerDoctors;
    private TextView txtDoctorCount, txtEmpty;
    private ProgressBar progressDoctors;
    private DoctorAdapter adapter;
    private JSONArray doctors = new JSONArray();
    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        session = new SessionActivity(this);

        recyclerDoctors = findViewById(R.id.recyclerDoctors);
        txtDoctorCount = findViewById(R.id.txtDoctorCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        progressDoctors = findViewById(R.id.progressDoctors);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnAddDoctor = findViewById(R.id.btnAddDoctor);

        btnBack.setOnClickListener(v -> finish());

        btnAddDoctor.setOnClickListener(v ->
                startActivity(new Intent(this, AddDoctorActivity.class)));

        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DoctorAdapter(
                doctors,
                this::editDoctor,
                this::confirmDeleteDoctor
        );

        recyclerDoctors.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDoctors();
    }

    private void loadDoctors() {
        progressDoctors.setVisibility(View.VISIBLE);
        txtEmpty.setVisibility(View.GONE);

        String token = session.getToken();

        ClientActivity.requestArray(
                "/doctors",
                "GET",
                token,
                new ClientActivity.ApiArrayCallback() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        runOnUiThread(() -> {
                            progressDoctors.setVisibility(View.GONE);

                            doctors = response;
                            adapter.updateData(doctors);

                            txtDoctorCount.setText(
                                    "Doctor List (" + doctors.length() + ")"
                            );

                            txtEmpty.setVisibility(
                                    doctors.length() == 0
                                            ? View.VISIBLE
                                            : View.GONE
                            );
                        });
                    }

                    @Override
                    public void onError(String message, int code) {
                        runOnUiThread(() -> {
                            progressDoctors.setVisibility(View.GONE);

                            Toast.makeText(
                                    DoctorActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }

    private void editDoctor(JSONObject doctor) {
        Intent intent = new Intent(this, EditDoctorActivity.class);
        intent.putExtra("doctor_data", doctor.toString());
        startActivity(intent);
    }

    private void confirmDeleteDoctor(JSONObject doctor) {
        String name = doctor.optString("name", "Doctor");
        String doctorId = doctor.optString("doctor_id");

        new AlertDialog.Builder(this)
                .setTitle("Delete Doctor")
                .setMessage("Are you sure you want to delete " + name + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    ClientActivity.request(
                            "/doctors/" + android.net.Uri.encode(doctorId),
                            "DELETE",
                            session.getToken(),
                            null,
                            new ClientActivity.ApiCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(
                                                DoctorActivity.this,
                                                "Doctor deleted successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadDoctors();
                                    });
                                }

                                @Override
                                public void onError(String message, int code) {
                                    runOnUiThread(() ->
                                            Toast.makeText(
                                                    DoctorActivity.this,
                                                    message,
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                                }
                            }
                    );
                })
                .show();
    }
}
