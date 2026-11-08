
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

        if (weekText.isEmpty()) {
            inputWeek.setError("Week is required");
            inputWeek.requestFocus();
            return;
        }

        if (monthText.isEmpty()) {
            inputMonth.setError("Month is required");
            inputMonth.requestFocus();
            return;
        }

        if (service.isEmpty()) {
            inputService.setError("Service is required");
            inputService.requestFocus();
            return;
        }

        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            int week = Integer.parseInt(weekText);
            int month = Integer.parseInt(monthText);

            if (week < 1 || week > 53) {
                inputWeek.setError("Week must be between 1 and 53");
                inputWeek.requestFocus();
                return;
            }

            if (month < 1 || month > 12) {
                inputMonth.setError("Month must be between 1 and 12");
                inputMonth.requestFocus();
                return;
            }

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

            ClientActivity.request(
                    "/hospital_beds",
                    "POST",
                    token,
                    body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(
                                        AddBedActivity.this,
                                        "Bed record added successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                setResult(RESULT_OK);
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(
                                            AddBedActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                        }
                    }
            );

        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Week, Month, and other numeric fields must contain valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Failed to create bed record: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
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
