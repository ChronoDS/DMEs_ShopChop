package com.example.danie.dmes_shopchop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// TODO make a "if allready logged in" function that runs on the onCreate function - leads to loginActivity if not signed in. else, lets say, MapsActivity for now.
// TODO for final review, make a splash screen instead of the current BetaPage exsists in MainActivity.
public class MainActivity extends AppCompatActivity {

    //Assignment 6 additions requested Example
    @SuppressLint("ParserError")
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


    // TODO HOW TO REACT IF A USER IS ALREADY REGISTERED

    //    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
//                    "User ID: " + user.getUid());
//        } else {
//            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
//                    "Error: sign in failed.");
//        }
//    }
    //    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
//    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(mTokenReceiver);
//    }


    ////////////////////////REGISTRATION WITH FIREBASE MAIL AND PASS FIELDS AND METHODS/////////////////////////
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////        updateUI(currentUser);
//        // THIS IS IF THE USER IS SIGNED IN - OPEN MAP ACTIVITY.
//    }
//
//
//
//


//    private void startSignIn() {
//        // Initiate sign in with custom token
//        // [START sign_in_custom]
//        mAuth.signInWithCustomToken(mCustomToken)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCustomToken:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
//                            Toast.makeText(CustomAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//        // [END sign_in_custom]
//    }


//    private void createAccount(String email, String password) {
//        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
//    }



//    private void setCustomToken(String token) {
//        mCustomToken = token;
//
//        String status;
//        if (mCustomToken != null) {
//            status = "Token:" + mCustomToken;
//        } else {
//            status = "Token: null";
//        }
//
//        // Enable/disable sign-in button and show the token
//        findViewById(R.id.button_sign_in).setEnabled((mCustomToken != null));
//        ((TextView) findViewById(R.id.text_token_status)).setText(status);
//    }

//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.button_sign_in) {
//            startSignIn();
//
//        }
//    }


    /////////////////////////////////END OF REGISTRATION WITH MAIL&PASS//////////////////////////////////////



}
