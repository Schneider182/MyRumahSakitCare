
package com.daffa.dokter2an;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class EditPatientActivity extends AppCompatActivity {

    private EditText inputName, inputGender, inputBirth;
    private EditText inputContact, inputAddress, inputInsurance;
    private EditText inputRegistration;

    private Button btnUpdatePatient, btnBack;

    private String patientId;
    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        session = new SessionActivity(this);

        TextView txtPatientId = findViewById(R.id.txtEditPatientId);

        inputName = findViewById(R.id.inputName);
        inputGender = findViewById(R.id.inputGender);
        inputBirth = findViewById(R.id.inputBirth);
        inputContact = findViewById(R.id.inputContact);
        inputAddress = findViewById(R.id.inputAddress);
        inputInsurance = findViewById(R.id.inputInsurance);
        inputRegistration = findViewById(R.id.inputRegistration);

        btnUpdatePatient = findViewById(R.id.btnUpdatePatient);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        loadPatientData();

        btnUpdatePatient.setOnClickListener(v -> updatePatient());
    }

    private void loadPatientData() {
        try {
            String data = getIntent().getStringExtra("patient_data");

            if (data == null) {
                Toast.makeText(this, R.string.patient_data_not_found, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            JSONObject patient = new JSONObject(data);

            patientId = patient.optString("patient_id", "");

            if (patientId.isEmpty()) {
                Toast.makeText(this, R.string.patient_id_missing, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            TextView txtPatientId = findViewById(R.id.txtEditPatientId);
            txtPatientId.setText(getString(R.string.patient_id_format, patientId));

            inputName.setText(getValue(patient, "name"));
            inputGender.setText(getValue(patient, "gender"));
            inputBirth.setText(getValue(patient, "date_of_birth"));
            inputContact.setText(getValue(patient, "contact_number"));
            inputAddress.setText(getValue(patient, "address"));
            inputInsurance.setText(getValue(patient, "insurance_provider"));
            inputRegistration.setText(getValue(patient, "registration_date"));

        } catch (JSONException e) {
            Toast.makeText(this, R.string.patient_data_read_failed, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getValue(JSONObject object, String key) {
        if (object.isNull(key)) {
            return "";
        }

        return object.optString(key, "");
    }

    private void updatePatient() {
        String name = inputName.getText().toString().trim();
        String gender = inputGender.getText().toString().trim();
        String birth = inputBirth.getText().toString().trim();
        String contact = inputContact.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String insurance = inputInsurance.getText().toString().trim();
        String registration = inputRegistration.getText().toString().trim();

        if (name.isEmpty() || gender.isEmpty() || birth.isEmpty()
                || contact.isEmpty() || address.isEmpty()
                || insurance.isEmpty() || registration.isEmpty()) {

            Toast.makeText(this, R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("name", name);
            body.put("gender", gender);
            body.put("date_of_birth", birth);
            body.put("contact_number", contact);
            body.put("address", address);
            body.put("insurance_provider", insurance);
            body.put("registration_date", registration);

        } catch (JSONException e) {
            Toast.makeText(this, R.string.prepare_data_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpdatePatient.setEnabled(false);
        btnUpdatePatient.setText(R.string.updating_patient);

        String token = session.getToken();
        String endpoint = "/patients/" + Uri.encode(patientId);

        ClientActivity.request(endpoint, "PUT", token, body, new ClientActivity.ApiCallback() {

            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            EditPatientActivity.this,
                            R.string.patient_updated,
                            Toast.LENGTH_SHORT
                    ).show();

                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String error, int code) {
                runOnUiThread(() -> {
                    btnUpdatePatient.setEnabled(true);
                    btnUpdatePatient.setText(R.string.update_patient);

                    Toast.makeText(
                            EditPatientActivity.this,
                            error,
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }
}
