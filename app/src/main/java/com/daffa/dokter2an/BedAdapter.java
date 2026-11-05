package com.daffa.dokter2an;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

public class BedAdapter extends RecyclerView.Adapter<BedAdapter.BedViewHolder> {

    public interface BedActionListener {
        void onEdit(JSONObject bed);
        void onDelete(JSONObject bed);
    }

    private JSONArray bedList;
    private final BedActionListener listener;

    public BedAdapter(JSONArray bedList, BedActionListener listener) {
        this.bedList = bedList;
        this.listener = listener;
    }

    public void updateData(JSONArray newBedList) {
        this.bedList = newBedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bed, parent, false);
        return new BedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BedViewHolder holder, int position) {
        JSONObject bed = bedList.optJSONObject(position);

        if (bed == null) return;

        holder.txtBedService.setText(bed.optString("service", "-"));
        holder.txtBedPeriod.setText(
                "Week " + bed.optInt("week") + " • Month " + bed.optInt("month")
        );

        holder.txtBedAvailable.setText(
                "Available Beds: " + displayValue(bed, "available_beds")
        );

        holder.txtBedPatients.setText(
                "Requests: " + displayValue(bed, "patients_request")
                        + "  |  Admitted: " + displayValue(bed, "patients_admitted")
                        + "  |  Refused: " + displayValue(bed, "patients_refused")
        );

        holder.txtBedOther.setText(
                "Satisfaction: " + displayValue(bed, "patient_satisfaction")
                        + "  |  Staff Morale: " + displayValue(bed, "staff_morale")
        );

        String event = bed.optString("event", "");
        holder.txtBedEvent.setText(
                event.isEmpty() || event.equals("null") ? "Event: -" : "Event: " + event
        );

        holder.btnEditBed.setOnClickListener(v -> listener.onEdit(bed));
        holder.btnDeleteBed.setOnClickListener(v -> listener.onDelete(bed));
    }

    private String displayValue(JSONObject object, String key) {
        if (object.isNull(key)) return "-";
        return object.optString(key, "-");
    }

    @Override
    public int getItemCount() {
        return bedList.length();
    }

    static class BedViewHolder extends RecyclerView.ViewHolder {

        TextView txtBedService, txtBedPeriod, txtBedAvailable;
        TextView txtBedPatients, txtBedOther, txtBedEvent;
        Button btnEditBed, btnDeleteBed;

        public BedViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBedService = itemView.findViewById(R.id.txtBedService);
            txtBedPeriod = itemView.findViewById(R.id.txtBedPeriod);
            txtBedAvailable = itemView.findViewById(R.id.txtBedAvailable);
            txtBedPatients = itemView.findViewById(R.id.txtBedPatients);
            txtBedOther = itemView.findViewById(R.id.txtBedOther);
            txtBedEvent = itemView.findViewById(R.id.txtBedEvent);
            btnEditBed = itemView.findViewById(R.id.btnEditBed);
            btnDeleteBed = itemView.findViewById(R.id.btnDeleteBed);
        }
    }
}