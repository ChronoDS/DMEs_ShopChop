
package com.example.danie.dmes_shopchop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class SignupActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static ImageButton ImgBtn;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    DatabaseReference ref = FirebaseAccess.getDatabaseReference();
    private FirebaseAuth mAuth;

    private static final String TAG = "EmailPasswrodAuth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAccess.getAuthInstance();

        //    Additions for photo menu and choosing from file:
        final String [] items           = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item ) {
                if (item == 0) {

                    dispatchTakePictureIntent();

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();

        ImgBtn = (ImageButton) findViewById(R.id.img_btn_picker);
        ImgBtn.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        dialog.show();
                    }
                }
        );
    }

    ////////////////////////REGISTRATION WITH FIREBASE MAIL AND PASS FIELDS AND METHODS/////////////////////////
    // TODO michael import this instance to FireAccess.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        // THIS IS IF THE USER IS SIGNED IN - OPEN MAP ACTIVITY.
    }

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

//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
//                    "User ID: " + user.getUid());
//        } else {
//            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
//                    "Error: sign in failed.");
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



    public void notImplementedYet(View view) {
        Toast.makeText(getApplicationContext(), "not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        String path = "";

        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImgBtn.setImageBitmap(imageBitmap);

            } else if (requestCode == PICK_FROM_FILE){//choice was made for pick from file.
                    if (data != null) {
                        Uri contentURI = data.getData();//the content itself
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                            path = saveImage(bitmap);
                            Toast.makeText(SignupActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                            ImgBtn.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(SignupActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }
        }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "John");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

//    // CHECKING if the user exists in DB and registers users.
//    private void userRegistration (){
//
//    }
//
//    private boolean isUsrExists(String user){
//
//
//        return true;
//    }
//
}
