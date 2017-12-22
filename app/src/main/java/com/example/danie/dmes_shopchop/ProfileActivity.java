package com.example.danie.dmes_shopchop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    // TODO pull relevant user data and display it in the Profile Activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void onTouch(View view) {
        Toast.makeText(getApplicationContext(), "not implemented yet", Toast.LENGTH_SHORT).show();
    }
}
