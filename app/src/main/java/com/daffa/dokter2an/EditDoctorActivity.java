
package com.daffa.dokter2an;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class EditDoctorActivity extends AppCompatActivity {

    private EditText inputName, inputSpecialization,
            inputPhone, inputExperience, inputBranch, inputEmail;

    private String doctorId;
    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor);

        session = new SessionActivity(this);

        inputName = findViewById(R.id.inputName);
        inputSpecialization = findViewById(R.id.inputSpecialization);
        inputPhone = findViewById(R.id.inputPhone);
        inputExperience = findViewById(R.id.inputExperience);
        inputBranch = findViewById(R.id.inputBranch);
        inputEmail = findViewById(R.id.inputEmail);

        Button btnSave = findViewById(R.id.btnSaveDoctor);
        Button btnBack = findViewById(R.id.btnBack);

        try {
            JSONObject doctor = new JSONObject(
                    getIntent().getStringExtra("doctor_data")
            );

            doctorId = doctor.optString("doctor_id");

            inputName.setText(doctor.optString("name", ""));
            inputSpecialization.setText(doctor.optString("specialization", ""));
            inputPhone.setText(doctor.optString("phone_number", ""));
            inputExperience.setText(doctor.optString("years_experience", ""));
            inputBranch.setText(doctor.optString("hospital_branch", ""));
            inputEmail.setText(doctor.optString("email", ""));

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Invalid doctor data",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> updateDoctor());
    }

    private void updateDoctor() {
        String name = inputName.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Doctor name is required");
            return;
        }

        try {
            JSONObject body = new JSONObject();

            body.put("name", name);
            body.put("specialization", inputSpecialization.getText().toString().trim());
            body.put("phone_number", inputPhone.getText().toString().trim());
            body.put("years_experience", inputExperience.getText().toString().trim());
            body.put("hospital_branch", inputBranch.getText().toString().trim());
            body.put("email", inputEmail.getText().toString().trim());

            ClientActivity.request(
                    "/doctors/" + android.net.Uri.encode(doctorId),
                    "PUT",
                    session.getToken(),
                    body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(
                                        EditDoctorActivity.this,
                                        "Doctor updated successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(
                                            EditDoctorActivity.this,
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
                    "Failed to update doctor data",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
