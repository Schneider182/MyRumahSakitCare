package com.daffa.dokter2an;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class EditStaffActivity extends AppCompatActivity {

    private SessionActivity session;

    private EditText inputName, inputRole, inputService;
    private String staffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

        session = new SessionActivity(this);

        inputName = findViewById(R.id.inputName);
        inputRole = findViewById(R.id.inputRole);
        inputService = findViewById(R.id.inputService);

        loadStaffData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveStaff).setOnClickListener(v -> updateStaff());
    }

    private void loadStaffData() {
        try {
            String data = getIntent().getStringExtra("staff_data");
            JSONObject staff = new JSONObject(data);

            staffId = staff.optString("staff_id");
            inputName.setText(staff.optString("staff_name", ""));
            inputRole.setText(staff.optString("role", ""));
            inputService.setText(staff.optString("service", ""));

        } catch (Exception e) {
            Toast.makeText(this,
                    "Failed to load staff data",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateStaff() {
        String name = inputName.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Staff name is required");
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("staff_name", name);
            body.put("role", inputRole.getText().toString().trim());
            body.put("service", inputService.getText().toString().trim());

            ClientActivity.request(
                    "/staff/" + Uri.encode(staffId),
                    "PUT",
                    session.getToken(),
                    body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(EditStaffActivity.this,
                                        "Staff updated successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(EditStaffActivity.this,
                                            message,
                                            Toast.LENGTH_SHORT).show());
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}