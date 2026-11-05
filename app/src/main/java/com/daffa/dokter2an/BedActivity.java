package com.daffa.dokter2an;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

public class BedActivity extends AppCompatActivity {

    private SessionActivity session;
    private BedAdapter adapter;
    private JSONArray bedList = new JSONArray();

    private TextView txtBedCount, txtEmpty;
    private ProgressBar progressBeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed);

        session = new SessionActivity(this);

        txtBedCount = findViewById(R.id.txtBedCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        progressBeds = findViewById(R.id.progressBeds);

        RecyclerView recyclerBeds = findViewById(R.id.recyclerBeds);
        recyclerBeds.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BedAdapter(bedList, new BedAdapter.BedActionListener() {
            @Override
            public void onEdit(JSONObject bed) {
                editBed(bed);
            }

            @Override
            public void onDelete(JSONObject bed) {
                confirmDeleteBed(bed);
            }
        });

        recyclerBeds.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddBed).setOnClickListener(v ->
                startActivity(new Intent(this, AddBedActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBeds();
    }

    private void loadBeds() {
        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.",
                    Toast.LENGTH_SHORT).show();
            session.clearSession();
            finish();
            return;
        }

        progressBeds.setVisibility(View.VISIBLE);
        txtEmpty.setVisibility(View.GONE);

        ClientActivity.requestArray("/hospital_beds", "GET", token,
                new ClientActivity.ApiArrayCallback() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        runOnUiThread(() -> {
                            bedList = response;
                            adapter.updateData(bedList);

                            txtBedCount.setText("Bed Records (" + bedList.length() + ")");
                            txtEmpty.setVisibility(
                                    bedList.length() == 0 ? View.VISIBLE : View.GONE
                            );

                            progressBeds.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onError(String message, int code) {
                        runOnUiThread(() -> {
                            progressBeds.setVisibility(View.GONE);
                            Toast.makeText(BedActivity.this,
                                    message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void editBed(JSONObject bed) {
        Intent intent = new Intent(this, EditBedActivity.class);
        intent.putExtra("bed_data", bed.toString());
        startActivity(intent);
    }

    private void confirmDeleteBed(JSONObject bed) {
        int week = bed.optInt("week");
        int month = bed.optInt("month");
        String service = bed.optString("service");

        new AlertDialog.Builder(this)
                .setTitle("Delete Bed Record")
                .setMessage("Delete the bed record for " + service
                        + " (Week " + week + ", Month " + month + ")?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String token = session.getToken();

                    String endpoint = "/hospital_beds/"
                            + week + "/" + month + "/" + Uri.encode(service);

                    ClientActivity.request(endpoint, "DELETE", token, null,
                            new ClientActivity.ApiCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(BedActivity.this,
                                                "Bed record deleted successfully",
                                                Toast.LENGTH_SHORT).show();
                                        loadBeds();
                                    });
                                }

                                @Override
                                public void onError(String message, int code) {
                                    runOnUiThread(() ->
                                            Toast.makeText(BedActivity.this,
                                                    message, Toast.LENGTH_SHORT).show());
                                }
                            });
                })
                .show();
    }
}