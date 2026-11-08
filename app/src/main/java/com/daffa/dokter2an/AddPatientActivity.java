
package com.daffa.dokter2an;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPatientActivity extends AppCompatActivity {

    private EditText inputName, inputPatientId;
    private EditText inputBirth, inputContact, inputAddress;
    private EditText inputInsurance, inputRegistration;

    private RadioGroup radioGender;

    private Button btnSavePatient;
    private SessionActivity session;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        session = new SessionActivity(this);

        inputName = findViewById(R.id.inputName);
        inputPatientId = findViewById(R.id.inputPatientId);
        radioGender = findViewById(R.id.radioGender);
        inputBirth = findViewById(R.id.inputBirth);
        inputContact = findViewById(R.id.inputContact);
        inputAddress = findViewById(R.id.inputAddress);
        inputInsurance = findViewById(R.id.inputInsurance);
        inputRegistration = findViewById(R.id.inputRegistration);

        btnSavePatient = findViewById(R.id.btnSavePatient);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        inputBirth.setOnClickListener(v -> showDatePicker(inputBirth));
        inputRegistration.setOnClickListener(v -> showDatePicker(inputRegistration));

        btnSavePatient.setOnClickListener(v -> savePatient());
    }

    private void showDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    target.setText(dateFormat.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void savePatient() {
        String name = inputName.getText().toString().trim();
        String patientId = inputPatientId.getText().toString().trim();

        int selectedGender = radioGender.getCheckedRadioButtonId();
        String gender = "";

        if (selectedGender == R.id.radioMale) {
            gender = "M";
        } else if (selectedGender == R.id.radioFemale) {
            gender = "F";
        }

        String birth = inputBirth.getText().toString().trim();
        String contact = inputContact.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String insurance = inputInsurance.getText().toString().trim();
        String registration = inputRegistration.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Patient name is required");
            inputName.requestFocus();
            return;
        }

        if (patientId.isEmpty()) {
            inputPatientId.setError("Patient ID is required");
            inputPatientId.requestFocus();
            return;
        }

        if (gender.isEmpty()) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (birth.isEmpty()) {
            Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contact.isEmpty()) {
            inputContact.setError("Contact number is required");
            inputContact.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            inputAddress.setError("Address is required");
            inputAddress.requestFocus();
            return;
        }

        if (insurance.isEmpty()) {
            inputInsurance.setError("Insurance provider is required");
            inputInsurance.requestFocus();
            return;
        }

        if (registration.isEmpty()) {
            Toast.makeText(this, "Please select registration date", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("name", name);
            body.put("patient_id", patientId);
            body.put("gender", gender);
            body.put("date_of_birth", birth);
            String phone = inputContact.getText().toString().trim();

            if (!phone.startsWith("+62")) {
                phone = "+62" + phone.replaceFirst("^0+", "");
            }

            body.put("phone_number", phone);
            body.put("address", address);
            body.put("insurance_provider", insurance);
            body.put("registration_date", registration);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to prepare data", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = session.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        btnSavePatient.setEnabled(false);
        btnSavePatient.setText("Saving...");

        ClientActivity.request(
                "/patients",
                "POST",
                token,
                body,
                new ClientActivity.ApiCallback() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    AddPatientActivity.this,
                                    "Patient added successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            setResult(RESULT_OK);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String message, int statusCode) {
                        runOnUiThread(() -> {
                            btnSavePatient.setEnabled(true);
                            btnSavePatient.setText("Save Patient");

                            Toast.makeText(
                                    AddPatientActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }
}
