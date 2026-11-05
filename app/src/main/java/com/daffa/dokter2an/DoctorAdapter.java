
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

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    public interface OnDoctorAction {
        void onAction(JSONObject doctor);
    }

    private JSONArray doctors;
    private final OnDoctorAction editListener;
    private final OnDoctorAction deleteListener;

    public DoctorAdapter(
            JSONArray doctors,
            OnDoctorAction editListener,
            OnDoctorAction deleteListener
    ) {
        this.doctors = doctors;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public void updateData(JSONArray newDoctors) {
        doctors = newDoctors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);

        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DoctorViewHolder holder,
            int position
    ) {
        JSONObject doctor = doctors.optJSONObject(position);

        if (doctor == null) return;

        holder.txtName.setText(
                doctor.optString("name", "-")
        );

        holder.txtDoctorId.setText(
                "ID: " + doctor.optString("doctor_id", "-")
        );

        holder.txtSpecialization.setText(
                "Specialization: " + doctor.optString("specialization", "-")
        );

        holder.txtPhone.setText(
                "Phone: " + doctor.optString("phone_number", "-")
        );

        holder.txtBranch.setText(
                "Branch: " + doctor.optString("hospital_branch", "-")
        );

        holder.btnEdit.setOnClickListener(v ->
                editListener.onAction(doctor)
        );

        holder.btnDelete.setOnClickListener(v ->
                deleteListener.onAction(doctor)
        );
    }

    @Override
    public int getItemCount() {
        return doctors.length();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtDoctorId;
        TextView txtSpecialization;
        TextView txtPhone;
        TextView txtBranch;

        Button btnEdit;
        Button btnDelete;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtDoctorName);
            txtDoctorId = itemView.findViewById(R.id.txtDoctorId);
            txtSpecialization = itemView.findViewById(R.id.txtDoctorSpecialization);
            txtPhone = itemView.findViewById(R.id.txtDoctorPhone);
            txtBranch = itemView.findViewById(R.id.txtDoctorBranch);

            btnEdit = itemView.findViewById(R.id.btnEditDoctor);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoctor);
        }
    }
}
