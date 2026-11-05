package com.daffa.dokter2an;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class EditBedActivity extends AppCompatActivity {

    private SessionActivity session;

    private EditText inputAvailableBeds, inputPatientsRequest;
    private EditText inputPatientsAdmitted, inputPatientsRefused;
    private EditText inputPatientSatisfaction, inputStaffMorale, inputEvent;

    private TextView txtBedIdentity;

    private int week, month;
    private String service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bed);

        session = new SessionActivity(this);

        txtBedIdentity = findViewById(R.id.txtBedIdentity);
        inputAvailableBeds = findViewById(R.id.inputAvailableBeds);
        inputPatientsRequest = findViewById(R.id.inputPatientsRequest);
        inputPatientsAdmitted = findViewById(R.id.inputPatientsAdmitted);
        inputPatientsRefused = findViewById(R.id.inputPatientsRefused);
        inputPatientSatisfaction = findViewById(R.id.inputPatientSatisfaction);
        inputStaffMorale = findViewById(R.id.inputStaffMorale);
        inputEvent = findViewById(R.id.inputEvent);

        loadBedData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveBed).setOnClickListener(v -> updateBed());
    }

    private void loadBedData() {
        try {
            String data = getIntent().getStringExtra("bed_data");
            JSONObject bed = new JSONObject(data);

            week = bed.getInt("week");
            month = bed.getInt("month");
            service = bed.getString("service");

            txtBedIdentity.setText(
                    service + " • Week " + week + " • Month " + month
            );

            setValue(inputAvailableBeds, bed, "available_beds");
            setValue(inputPatientsRequest, bed, "patients_request");
            setValue(inputPatientsAdmitted, bed, "patients_admitted");
            setValue(inputPatientsRefused, bed, "patients_refused");
            setValue(inputPatientSatisfaction, bed, "patient_satisfaction");
            setValue(inputStaffMorale, bed, "staff_morale");
            setValue(inputEvent, bed, "event");

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load bed record",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setValue(EditText input, JSONObject bed, String key) {
        if (!bed.isNull(key)) {
            input.setText(bed.optString(key, ""));
        }
    }

    private void updateBed() {
        try {
            JSONObject body = new JSONObject();

            putInteger(body, "available_beds", inputAvailableBeds);
            putInteger(body, "patients_request", inputPatientsRequest);
            putInteger(body, "patients_admitted", inputPatientsAdmitted);
            putInteger(body, "patients_refused", inputPatientsRefused);
            putInteger(body, "patient_satisfaction", inputPatientSatisfaction);
            putInteger(body, "staff_morale", inputStaffMorale);

            String event = inputEvent.getText().toString().trim();
            body.put("event", event.isEmpty() ? JSONObject.NULL : event);

            String endpoint = "/hospital_beds/"
                    + week + "/" + month + "/" + Uri.encode(service);

            ClientActivity.request(endpoint, "PUT", session.getToken(), body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(EditBedActivity.this,
                                        "Bed record updated successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(EditBedActivity.this,
                                            message, Toast.LENGTH_SHORT).show());
                        }
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "All numeric fields must contain valid integers",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void putInteger(JSONObject body, String key, EditText input)
            throws Exception {
        String value = input.getText().toString().trim();

        if (value.isEmpty()) {
            body.put(key, JSONObject.NULL);
        } else {
            body.put(key, Integer.parseInt(value));
        }
    }
}