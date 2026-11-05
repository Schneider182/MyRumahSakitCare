
package com.daffa.dokter2an;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class AddDoctorActivity extends AppCompatActivity {

    private EditText inputName, inputDoctorId, inputSpecialization,
            inputPhone, inputExperience, inputBranch, inputEmail;

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
            return;
        }

        if (doctorId.isEmpty()) {
            inputDoctorId.setError("Doctor ID is required");
            return;
        }

        try {
            JSONObject body = new JSONObject();

            body.put("name", name);
            body.put("doctor_id", doctorId);
            body.put("specialization", inputSpecialization.getText().toString().trim());
            body.put("phone_number", inputPhone.getText().toString().trim());
            body.put("years_experience", inputExperience.getText().toString().trim());
            body.put("hospital_branch", inputBranch.getText().toString().trim());
            body.put("email", inputEmail.getText().toString().trim());

            ClientActivity.request(
                    "/doctors",
                    "POST",
                    session.getToken(),
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

                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(
                                            AddDoctorActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
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
