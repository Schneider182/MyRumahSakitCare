
package com.daffa.dokter2an;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.BillingViewHolder> {

    public interface BillingActionListener {
        void onEdit(JSONObject billing);
        void onDelete(JSONObject billing);
    }

    private JSONArray billingList;
    private final BillingActionListener listener;

    public BillingAdapter(JSONArray billingList, BillingActionListener listener) {
        this.billingList = billingList;
        this.listener = listener;
    }

    public void updateData(JSONArray newList) {
        billingList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_billing, parent, false);
        return new BillingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingViewHolder holder, int position) {
        JSONObject billing = billingList.optJSONObject(position);
        if (billing == null) return;

        String billId = billing.optString("bill_id", "-");
        String patientId = billing.optString("patient_id", "-");
        String amount = billing.optString("amount", "-");
        String date = billing.optString("bill_date", "-");
        String method = billing.optString("payment_method", "-");
        String status = billing.optString("payment_status", "-");

        holder.txtBillId.setText("Bill ID: " + billId);
        holder.txtBillPatient.setText("Patient ID: " + patientId);
        holder.txtBillAmount.setText("Amount: " + amount);
        holder.txtBillDate.setText("Date: " + date);
        holder.txtBillPayment.setText("Payment: " + method + " • " + status);

        holder.btnEditBilling.setOnClickListener(v -> listener.onEdit(billing));
        holder.btnDeleteBilling.setOnClickListener(v -> listener.onDelete(billing));
    }

    @Override
    public int getItemCount() {
        return billingList.length();
    }

    static class BillingViewHolder extends RecyclerView.ViewHolder {
        TextView txtBillId, txtBillPatient, txtBillAmount, txtBillDate, txtBillPayment;
        Button btnEditBilling, btnDeleteBilling;

        BillingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBillId = itemView.findViewById(R.id.txtBillId);
            txtBillPatient = itemView.findViewById(R.id.txtBillPatient);
            txtBillAmount = itemView.findViewById(R.id.txtBillAmount);
            txtBillDate = itemView.findViewById(R.id.txtBillDate);
            txtBillPayment = itemView.findViewById(R.id.txtBillPayment);
            btnEditBilling = itemView.findViewById(R.id.btnEditBilling);
            btnDeleteBilling = itemView.findViewById(R.id.btnDeleteBilling);
        }
    }
}
