
package com.daffa.dokter2an;

import android.os.Bundle;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class EditBillingActivity extends AppCompatActivity {

    private SessionActivity session;
    private String billId;
    private EditText inputPatientId, inputTreatmentId;
    private EditText inputBillDate, inputAmount, inputPaymentMethod, inputPaymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_billing);

        session = new SessionActivity(this);

        TextView txtBillIdentity = findViewById(R.id.txtBillIdentity);
        inputPatientId = findViewById(R.id.inputPatientId);
        inputTreatmentId = findViewById(R.id.inputTreatmentId);
        inputBillDate = findViewById(R.id.inputBillDate);
        inputAmount = findViewById(R.id.inputAmount);
        inputPaymentMethod = findViewById(R.id.inputPaymentMethod);
        inputPaymentStatus = findViewById(R.id.inputPaymentStatus);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnSaveBilling = findViewById(R.id.btnSaveBilling);

        btnBack.setOnClickListener(v -> finish());
        btnSaveBilling.setOnClickListener(v -> updateBilling());

        try {
            JSONObject billing = new JSONObject(getIntent().getStringExtra("billing_data"));

            billId = billing.optString("bill_id");
            txtBillIdentity.setText("Bill ID: " + billId);

            inputPatientId.setText(value(billing, "patient_id"));
            inputTreatmentId.setText(value(billing, "treatment_id"));
            inputBillDate.setText(value(billing, "bill_date"));
            inputAmount.setText(value(billing, "amount"));
            inputPaymentMethod.setText(value(billing, "payment_method"));
            inputPaymentStatus.setText(value(billing, "payment_status"));

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load billing data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateBilling() {
        String patientId = inputPatientId.getText().toString().trim();

        if (patientId.isEmpty()) {
            Toast.makeText(this, "Patient ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("patient_id", patientId);
            body.put("treatment_id", nullable(inputTreatmentId));
            body.put("bill_date", nullable(inputBillDate));
            body.put("amount", nullable(inputAmount));
            body.put("payment_method", nullable(inputPaymentMethod));
            body.put("payment_status", nullable(inputPaymentStatus));

            ClientActivity.request("/billing/" + Uri.encode(billId), "PUT",
                    session.getToken(), body, new ClientActivity.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> {
                                Toast.makeText(EditBillingActivity.this, "Billing updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message, int code) {
                            runOnUiThread(() ->
                                    Toast.makeText(EditBillingActivity.this, message, Toast.LENGTH_LONG).show());
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

    private String value(JSONObject object, String key) {
        return object.isNull(key) ? "" : object.optString(key, "");
    }
}
