package com.example.danie.dmes_shopchop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ReadMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_me);
    }

    protected void btn_Start(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
