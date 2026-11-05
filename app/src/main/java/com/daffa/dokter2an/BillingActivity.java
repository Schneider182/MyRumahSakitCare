
package com.daffa.dokter2an;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class BillingActivity extends AppCompatActivity {

    private SessionActivity session;
    private BillingAdapter adapter;
    private JSONArray billingList = new JSONArray();
    private TextView txtBillingCount, txtEmpty;
    private ProgressBar progressBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        session = new SessionActivity(this);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnAddBilling = findViewById(R.id.btnAddBilling);
        txtBillingCount = findViewById(R.id.txtBillingCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        progressBilling = findViewById(R.id.progressBilling);

        RecyclerView recyclerBilling = findViewById(R.id.recyclerBilling);
        recyclerBilling.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillingAdapter(billingList, new BillingAdapter.BillingActionListener() {
            @Override
            public void onEdit(JSONObject billing) {
                Intent intent = new Intent(BillingActivity.this, EditBillingActivity.class);
                intent.putExtra("billing_data", billing.toString());
                startActivity(intent);
            }

            @Override
            public void onDelete(JSONObject billing) {
                confirmDeleteBilling(billing);
            }
        });

        recyclerBilling.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnAddBilling.setOnClickListener(v ->
                startActivity(new Intent(this, AddBillingActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBilling();
    }

    private void loadBilling() {
        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            session.clearSession();
            finish();
            return;
        }

        progressBilling.setVisibility(android.view.View.VISIBLE);
        txtEmpty.setVisibility(android.view.View.GONE);

        ClientActivity.requestArray("/billing", "GET", token, new ClientActivity.ApiArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                runOnUiThread(() -> {
                    progressBilling.setVisibility(android.view.View.GONE);
                    billingList = response;
                    adapter.updateData(response);
                    txtBillingCount.setText("Total Billing: " + response.length());
                    txtEmpty.setVisibility(response.length() == 0 ? android.view.View.VISIBLE : android.view.View.GONE);
                });
            }

            @Override
            public void onError(String message, int code) {
                runOnUiThread(() -> {
                    progressBilling.setVisibility(android.view.View.GONE);
                    Toast.makeText(BillingActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void confirmDeleteBilling(JSONObject billing) {
        String billId = billing.optString("bill_id");

        new AlertDialog.Builder(this)
                .setTitle("Delete Billing")
                .setMessage("Delete billing " + billId + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String token = session.getToken();

                    ClientActivity.request(
                            "/billing/" + android.net.Uri.encode(billId),
                            "DELETE",
                            token,
                            null,
                            new ClientActivity.ApiCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(BillingActivity.this, "Billing deleted", Toast.LENGTH_SHORT).show();
                                        loadBilling();
                                    });
                                }

                                @Override
                                public void onError(String message, int code) {
                                    runOnUiThread(() ->
                                            Toast.makeText(BillingActivity.this, message, Toast.LENGTH_LONG).show());
                                }
                            }
                    );
                })
                .show();
    }
}
