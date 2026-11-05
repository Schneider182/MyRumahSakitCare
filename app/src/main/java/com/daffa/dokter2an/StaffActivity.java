package com.daffa.dokter2an;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

public class StaffActivity extends AppCompatActivity {

    private SessionActivity session;
    private StaffAdapter adapter;
    private JSONArray staffList = new JSONArray();

    private TextView txtStaffCount;
    private TextView txtEmpty;
    private ProgressBar progressStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        session = new SessionActivity(this);

        txtStaffCount = findViewById(R.id.txtStaffCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        progressStaff = findViewById(R.id.progressStaff);

        RecyclerView recyclerStaff = findViewById(R.id.recyclerStaff);
        recyclerStaff.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StaffAdapter(staffList, new StaffAdapter.StaffActionListener() {
            @Override
            public void onEdit(JSONObject staff) {
                editStaff(staff);
            }

            @Override
            public void onDelete(JSONObject staff) {
                confirmDeleteStaff(staff);
            }
        });

        recyclerStaff.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddStaff).setOnClickListener(v ->
                startActivity(new Intent(this, AddStaffActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStaff();
    }

    private void loadStaff() {
        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            session.clearSession();
            finish();
            return;
        }

        progressStaff.setVisibility(android.view.View.VISIBLE);
        txtEmpty.setVisibility(android.view.View.GONE);

        ClientActivity.requestArray("/staff", "GET", token,
                new ClientActivity.ApiArrayCallback() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        runOnUiThread(() -> {
                            staffList = response;
                            adapter.updateData(staffList);

                            txtStaffCount.setText("Staff List (" + staffList.length() + ")");
                            txtEmpty.setVisibility(
                                    staffList.length() == 0
                                            ? android.view.View.VISIBLE
                                            : android.view.View.GONE
                            );

                            progressStaff.setVisibility(android.view.View.GONE);
                        });
                    }

                    @Override
                    public void onError(String message, int code) {
                        runOnUiThread(() -> {
                            progressStaff.setVisibility(android.view.View.GONE);
                            Toast.makeText(StaffActivity.this,
                                    message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void editStaff(JSONObject staff) {
        Intent intent = new Intent(this, EditStaffActivity.class);
        intent.putExtra("staff_data", staff.toString());
        startActivity(intent);
    }

    private void confirmDeleteStaff(JSONObject staff) {
        String staffName = staff.optString("staff_name");
        String staffId = staff.optString("staff_id");

        new AlertDialog.Builder(this)
                .setTitle("Delete Staff")
                .setMessage("Are you sure you want to delete " + staffName + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String token = session.getToken();

                    ClientActivity.request(
                            "/staff/" + Uri.encode(staffId),
                            "DELETE",
                            token,
                            null,
                            new ClientActivity.ApiCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(StaffActivity.this,
                                                "Staff deleted successfully",
                                                Toast.LENGTH_SHORT).show();
                                        loadStaff();
                                    });
                                }

                                @Override
                                public void onError(String message, int code) {
                                    runOnUiThread(() ->
                                            Toast.makeText(StaffActivity.this,
                                                    message,
                                                    Toast.LENGTH_SHORT).show());
                                }
                            }
                    );
                })
                .show();
    }
}