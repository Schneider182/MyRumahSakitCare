package com.daffa.dokter2an;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private final JSONArray patients;
    private final Context context;

    public PatientAdapter(Context context, JSONArray patients) {
        this.context = context;
        this.patients = patients;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);

        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        JSONObject patient = patients.optJSONObject(position);

        if (patient == null) return;

        String patientId = getValue(patient, "patient_id");
        String patientName = getValue(patient, "name");

        holder.txtPatientName.setText(patientName);
        holder.txtPatientId.setText("ID: " + patientId);
        holder.txtPatientGender.setText("Gender: " + getValue(patient, "gender"));
        holder.txtPatientBirth.setText("Date of Birth: " + getValue(patient, "date_of_birth"));
        holder.txtPatientContact.setText("Contact: " + getValue(patient, "contact_number"));
        holder.txtPatientAddress.setText("Address: " + getValue(patient, "address"));
        holder.txtPatientInsurance.setText("Insurance: " + getValue(patient, "insurance_provider"));

        holder.btnEditPatient.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPatientActivity.class);
            intent.putExtra("patient_data", patient.toString());
            context.startActivity(intent);
        });

        holder.btnDeletePatient.setOnClickListener(v -> {
            if (context instanceof PatientActivity) {
                ((PatientActivity) context).confirmDeletePatient(
                        patientId,
                        patientName
                );
            }
        });
    }

    private String getValue(JSONObject patient, String key) {
        if (patient.isNull(key)) {
            return "-";
        }

        String value = patient.optString(key, "-");
        return value.isEmpty() ? "-" : value;
    }

    @Override
    public int getItemCount() {
        return patients.length();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {

        TextView txtPatientName, txtPatientId;
        TextView txtPatientGender, txtPatientBirth;
        TextView txtPatientContact, txtPatientAddress;
        TextView txtPatientInsurance;

        MaterialButton btnEditPatient;
        MaterialButton btnDeletePatient;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPatientName = itemView.findViewById(R.id.txtPatientName);
            txtPatientId = itemView.findViewById(R.id.txtPatientId);
            txtPatientGender = itemView.findViewById(R.id.txtPatientGender);
            txtPatientBirth = itemView.findViewById(R.id.txtPatientBirth);
            txtPatientContact = itemView.findViewById(R.id.txtPatientContact);
            txtPatientAddress = itemView.findViewById(R.id.txtPatientAddress);
            txtPatientInsurance = itemView.findViewById(R.id.txtPatientInsurance);

            btnEditPatient = itemView.findViewById(R.id.btnEditPatient);
            btnDeletePatient = itemView.findViewById(R.id.btnDeletePatient);
        }
    }
}