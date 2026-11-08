
package com.daffa.dokter2an;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class AddDoctorActivity extends AppCompatActivity {

    private EditText inputName, inputDoctorId, inputSpecialization;
    private EditText inputPhone, inputExperience, inputBranch, inputEmail;

    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        session = new SessionActivity(this);

        inputName = findViewById(R.id.inputName);
        inputDoctorId = findViewById(R.id.inputDoctorId);
        inputSpecialization = findViewById(R.id.inputSpecialization);
        inputPhone = findViewById(R.id.inputPhone);
        inputExperience = findViewById(R.id.inputExperience);
        inputBranch = findViewById(R.id.inputBranch);
        inputEmail = findViewById(R.id.inputEmail);

        Button btnSave = findViewById(R.id.btnSaveDoctor);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveDoctor());
    }

    private void saveDoctor() {
        String name = inputName.getText().toString().trim();
        String doctorId = inputDoctorId.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Doctor name is required");
            inputName.requestFocus();
            return;
        }

        if (doctorId.isEmpty()) {
            inputDoctorId.setError("Doctor ID is required");
            inputDoctorId.requestFocus();
            return;
        }

        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JSONObject body = new JSONObject();

            body.put("name", name);
            body.put("doctor_id", doctorId);
            body.put("specialization", inputSpecialization.getText().toString().trim());
            String phone = inputPhone.getText().toString().trim();

            if (!phone.startsWith("+62")) {
                phone = "+62" + phone.replaceFirst("^0+", "");
            }

            body.put("phone_number", phone);
            body.put("years_experience", inputExperience.getText().toString().trim());
            body.put("hospital_branch", inputBranch.getText().toString().trim());
            body.put("email", inputEmail.getText().toString().trim());

            ClientActivity.request(
                    "/doctors",
                    "POST",
                    token,
                    body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(
                                        AddDoctorActivity.this,
                                        "Doctor added successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                setResult(RESULT_OK);
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() -> {
                                Toast.makeText(
                                        AddDoctorActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                        }
                    }
            );

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Failed to create doctor data",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
