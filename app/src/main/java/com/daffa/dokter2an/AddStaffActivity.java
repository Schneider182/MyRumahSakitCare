package com.daffa.dokter2an;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class AddStaffActivity extends AppCompatActivity {

    private SessionActivity session;

    private EditText inputName, inputStaffId, inputRole, inputService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        session = new SessionActivity(this);

        inputName = findViewById(R.id.inputName);
        inputStaffId = findViewById(R.id.inputStaffId);
        inputRole = findViewById(R.id.inputRole);
        inputService = findViewById(R.id.inputService);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveStaff).setOnClickListener(v -> saveStaff());
    }

    private void saveStaff() {
        String name = inputName.getText().toString().trim();
        String staffId = inputStaffId.getText().toString().trim();

        if (name.isEmpty() || staffId.isEmpty()) {
            Toast.makeText(this,
                    "Staff name and Staff ID are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("staff_name", name);
            body.put("staff_id", staffId);
            body.put("role", inputRole.getText().toString().trim());
            body.put("service", inputService.getText().toString().trim());

            ClientActivity.request("/staff", "POST", session.getToken(), body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(AddStaffActivity.this,
                                        "Staff added successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(AddStaffActivity.this,
                                            message,
                                            Toast.LENGTH_SHORT).show());
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}