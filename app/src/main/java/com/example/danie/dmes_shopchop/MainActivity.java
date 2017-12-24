package com.example.danie.dmes_shopchop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// TODO make a "if allready logged in" function that runs on the onCreate function - leads to loginActivity if not signed in. else, lets say, MapsActivity for now.
// TODO for final review, make a splash screen instead of the current BetaPage exsists in MainActivity.
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Users");
        DatabaseReference myRef;
        myRef = FirebaseAccess.getDatabaseReference();

//        String userId = myRef.push().getKey();
//        String userId = "9GlOoGvaEyaprEWG2UKihOJXXkn2";
//        myRef.child("users").child(userId).child("name").setValue("Elad Test User");
//        myRef.child("users").child(userId).child("phone").setValue("050-665-5566");
//        myRef.child("users").child(userId).child("email").setValue("ep@devtest.com");
    }

    protected void onClickSignUpActivity(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    protected void onClickSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    protected void onClickLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void onClickReadme(View view){
        Intent intent = new Intent(this, ReadMeActivity.class);
        startActivity(intent);
    }

    protected void onClickMaps(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    protected void onClickProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
