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

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    public interface StaffActionListener {
        void onEdit(JSONObject staff);
        void onDelete(JSONObject staff);
    }

    private JSONArray staffList;
    private final StaffActionListener listener;

    public StaffAdapter(JSONArray staffList, StaffActionListener listener) {
        this.staffList = staffList;
        this.listener = listener;
    }

    public void updateData(JSONArray newStaffList) {
        this.staffList = newStaffList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        JSONObject staff = staffList.optJSONObject(position);

        if (staff == null) return;

        holder.txtStaffName.setText(staff.optString("staff_name", "-"));
        holder.txtStaffId.setText("ID: " + staff.optString("staff_id", "-"));
        holder.txtStaffRole.setText("Role: " + staff.optString("role", "-"));
        holder.txtStaffService.setText("Service: " + staff.optString("service", "-"));

        holder.btnEditStaff.setOnClickListener(v ->
                listener.onEdit(staff));

        holder.btnDeleteStaff.setOnClickListener(v ->
                listener.onDelete(staff));
    }

    @Override
    public int getItemCount() {
        return staffList.length();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {

        TextView txtStaffName, txtStaffId, txtStaffRole, txtStaffService;
        Button btnEditStaff, btnDeleteStaff;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);

            txtStaffName = itemView.findViewById(R.id.txtStaffName);
            txtStaffId = itemView.findViewById(R.id.txtStaffId);
            txtStaffRole = itemView.findViewById(R.id.txtStaffRole);
            txtStaffService = itemView.findViewById(R.id.txtStaffService);
            btnEditStaff = itemView.findViewById(R.id.btnEditStaff);
            btnDeleteStaff = itemView.findViewById(R.id.btnDeleteStaff);
        }
    }
}