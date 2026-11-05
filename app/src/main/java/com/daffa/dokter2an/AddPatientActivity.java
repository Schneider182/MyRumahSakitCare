
package com.daffa.dokter2an;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class AddPatientActivity extends AppCompatActivity {

    private EditText inputName, inputPatientId, inputGender;
    private EditText inputBirth, inputContact, inputAddress;
    private EditText inputInsurance, inputRegistration;

    private Button btnSavePatient;
    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        session = new SessionActivity(this);

        inputName = findViewById(R.id.inputName);
        inputPatientId = findViewById(R.id.inputPatientId);
        inputGender = findViewById(R.id.inputGender);
        inputBirth = findViewById(R.id.inputBirth);
        inputContact = findViewById(R.id.inputContact);
        inputAddress = findViewById(R.id.inputAddress);
        inputInsurance = findViewById(R.id.inputInsurance);
        inputRegistration = findViewById(R.id.inputRegistration);

        btnSavePatient = findViewById(R.id.btnSavePatient);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSavePatient.setOnClickListener(v -> savePatient());
    }

    private void savePatient() {
        String name = inputName.getText().toString().trim();
        String patientId = inputPatientId.getText().toString().trim();
        String gender = inputGender.getText().toString().trim();
        String birth = inputBirth.getText().toString().trim();
        String contact = inputContact.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String insurance = inputInsurance.getText().toString().trim();
        String registration = inputRegistration.getText().toString().trim();

        if (name.isEmpty() || patientId.isEmpty() || gender.isEmpty()
                || birth.isEmpty() || contact.isEmpty()
                || address.isEmpty() || insurance.isEmpty()
                || registration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("name", name);
            body.put("patient_id", patientId);
            body.put("gender", gender);
            body.put("date_of_birth", birth);
            body.put("contact_number", contact);
            body.put("address", address);
            body.put("insurance_provider", insurance);
            body.put("registration_date", registration);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to prepare data", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSavePatient.setEnabled(false);
        btnSavePatient.setText("Saving...");

        ClientActivity.request(
                "/patients",
                "POST",
                token,
                body,
                new ClientActivity.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    AddPatientActivity.this,
                                    "Patient added successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            setResult(RESULT_OK);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String message, int statusCode) {
                        runOnUiThread(() -> {
                            btnSavePatient.setEnabled(true);
                            btnSavePatient.setText("Save Patient");

                            Toast.makeText(
                                    AddPatientActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }
}
