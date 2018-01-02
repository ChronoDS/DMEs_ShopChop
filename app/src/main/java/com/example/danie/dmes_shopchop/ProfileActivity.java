package com.example.danie.dmes_shopchop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.zip.Inflater;

public class ProfileActivity extends AppCompatActivity {

    // Image Capture/Choosing variables:
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static SelectableRoundedImageView RndImg;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    //

    // User Profile Details:
    private FirebaseUser user;
    DatabaseReference dbRef;
    //

    // VISUAL PROFILE ELEMENTS

    private TextView fullname;
    private TextView phonenum;
    private TextView emailadd;
    private String userId;

    //

    // TODO pull relevant user data and display it in the Profile Activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullname = (TextView) findViewById(R.id.pName);
        phonenum = (TextView)findViewById(R.id.pPhone);
        emailadd = (TextView)findViewById(R.id.pEmail);
        dbRef = FirebaseAccess.getDatabaseReference();
        userId = dbRef.push().getKey();
        GetUserData();



        //** Additions for photo menu and choosing from file:
        final String [] items           = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item ) {
                if (item == 0) {

//                    IA.dispatchTakePictureIntent();
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

        RndImg = (SelectableRoundedImageView) findViewById(R.id.btnProfilePic);
        RndImg.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        dialog.show();
                    }
                }
        );
        //**
    }

    //******************* IMAGE MANAGEMENT **********************//
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
                RndImg.setImageBitmap(imageBitmap);

            } else if (requestCode == PICK_FROM_FILE){//choice was made for pick from file.
                if (data != null) {
                    Uri contentURI = data.getData();//the content itself
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        path = saveImage(bitmap);
                        Toast.makeText(ProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                        RndImg.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    //**********************************************************//
    //******************* USER MANAGEMENT **********************//
    protected void GetUserData() {
        user = FirebaseAccess.getAuthInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();

            // Initialize Fields for profile:

            dbRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            try {
                                fullname.setText(dataSnapshot.child("name").getValue().toString());
                                phonenum.setText(dataSnapshot.child("phone").getValue().toString());
                                emailadd.setText(dataSnapshot.child("email").getValue().toString());
                                Log.e("TAG", "" + dataSnapshot.getValue()); // your name values you will get here
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            Log.e("TAG", " it's null.");
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
            ////////////////////////////////////

//            fullname.setText(name);

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }


    //**********************************************************//


    public void onTouch(View view) {
        Toast.makeText(getApplicationContext(), "not implemented yet", Toast.LENGTH_SHORT).show();
    }
}
