
package com.daffa.dokter2an;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class AddBillingActivity extends AppCompatActivity {

    private SessionActivity session;
    private EditText inputBillId, inputPatientId, inputTreatmentId;
    private EditText inputBillDate, inputAmount, inputPaymentMethod, inputPaymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_billing);

        session = new SessionActivity(this);

        inputBillId = findViewById(R.id.inputBillId);
        inputPatientId = findViewById(R.id.inputPatientId);
        inputTreatmentId = findViewById(R.id.inputTreatmentId);
        inputBillDate = findViewById(R.id.inputBillDate);
        inputAmount = findViewById(R.id.inputAmount);
        inputPaymentMethod = findViewById(R.id.inputPaymentMethod);
        inputPaymentStatus = findViewById(R.id.inputPaymentStatus);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnSaveBilling = findViewById(R.id.btnSaveBilling);

        btnBack.setOnClickListener(v -> finish());
        btnSaveBilling.setOnClickListener(v -> saveBilling());
    }

    private void saveBilling() {
        String billId = inputBillId.getText().toString().trim();
        String patientId = inputPatientId.getText().toString().trim();

        if (billId.isEmpty() || patientId.isEmpty()) {
            Toast.makeText(this, "Bill ID and Patient ID are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("bill_id", billId);
            body.put("patient_id", patientId);
            body.put("treatment_id", nullable(inputTreatmentId));
            body.put("bill_date", nullable(inputBillDate));
            body.put("amount", nullable(inputAmount));
            body.put("payment_method", nullable(inputPaymentMethod));
            body.put("payment_status", nullable(inputPaymentStatus));

            ClientActivity.request("/billing", "POST", session.getToken(), body,
                    new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(AddBillingActivity.this, "Billing added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(AddBillingActivity.this, message, Toast.LENGTH_LONG).show());
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Object nullable(EditText input) {
        String value = input.getText().toString().trim();
        return value.isEmpty() ? JSONObject.NULL : value;
    }
}
