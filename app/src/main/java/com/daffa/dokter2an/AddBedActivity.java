package com.daffa.dokter2an;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class AddBedActivity extends AppCompatActivity {

    private SessionActivity session;

    private EditText inputWeek, inputMonth, inputService;
    private EditText inputAvailableBeds, inputPatientsRequest;
    private EditText inputPatientsAdmitted, inputPatientsRefused;
    private EditText inputPatientSatisfaction, inputStaffMorale, inputEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bed);

        session = new SessionActivity(this);

        inputWeek = findViewById(R.id.inputWeek);
        inputMonth = findViewById(R.id.inputMonth);
        inputService = findViewById(R.id.inputService);
        inputAvailableBeds = findViewById(R.id.inputAvailableBeds);
        inputPatientsRequest = findViewById(R.id.inputPatientsRequest);
        inputPatientsAdmitted = findViewById(R.id.inputPatientsAdmitted);
        inputPatientsRefused = findViewById(R.id.inputPatientsRefused);
        inputPatientSatisfaction = findViewById(R.id.inputPatientSatisfaction);
        inputStaffMorale = findViewById(R.id.inputStaffMorale);
        inputEvent = findViewById(R.id.inputEvent);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveBed).setOnClickListener(v -> saveBed());
    }

    private void saveBed() {
        String weekText = inputWeek.getText().toString().trim();
        String monthText = inputMonth.getText().toString().trim();
        String service = inputService.getText().toString().trim();

        if (weekText.isEmpty() || monthText.isEmpty() || service.isEmpty()) {
            Toast.makeText(this,
                    "Week, Month, and Service are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int week = Integer.parseInt(weekText);
            int month = Integer.parseInt(monthText);

            JSONObject body = new JSONObject();
            body.put("week", week);
            body.put("month", month);
            body.put("service", service);

            putInteger(body, "available_beds", inputAvailableBeds);
            putInteger(body, "patients_request", inputPatientsRequest);
            putInteger(body, "patients_admitted", inputPatientsAdmitted);
            putInteger(body, "patients_refused", inputPatientsRefused);
            putInteger(body, "patient_satisfaction", inputPatientSatisfaction);
            putInteger(body, "staff_morale", inputStaffMorale);

            String event = inputEvent.getText().toString().trim();
            body.put("event", event.isEmpty() ? JSONObject.NULL : event);

            ClientActivity.request("/hospital_beds", "POST",
                    session.getToken(), body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(AddBedActivity.this,
                                        "Bed record added successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(AddBedActivity.this,
                                            message, Toast.LENGTH_SHORT).show());
                        }
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Week and Month must be valid numbers",
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