package com.daffa.dokter2an;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReceptionistActivity extends AppCompatActivity {

    private SessionActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        session = new SessionActivity(this);

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        session.clearSession();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}