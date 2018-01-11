package com.example.danie.dmes_shopchop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joooonho.SelectableRoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    // Image Upload variables
    private StorageReference mStorageRef;
    UploadTask uploadTask;
    StorageMetadata metadata;
    //

    // full image capture
    String mCurrentPhotoPath;
    //

    // TODO pull relevant user data and display it in the Profile Activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        // Create a storage reference from our app
        mStorageRef = FirebaseStorage.getInstance().getReference();
//        downloadImageFromStorage();

        // Create a reference
        StorageReference mProfileRef = mStorageRef.child("profile_"+userId+".jpg");

        // Create a reference to 'jpg'
        StorageReference mProfileImagesRef = mStorageRef.child("profileImages/profile_"+userId+".jpg");


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

    // TODO NEED A WORKING PHOTO MAKING METHOD.

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }
    //******************* IMAGE MANAGEMENT **********************//
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        Bitmap bitmap = null;
        String path = "";

        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                RndImg.setImageBitmap(imageBitmap);

                ///////////////UPLOADING IMAGE TO FIREBASE STORAGE/////////////////////
//                Uri contentURI = data.getData();
//                uploadImageToStorage(contentURI);

            } else if (requestCode == PICK_FROM_FILE){//choice was made for pick from file.
                if (data != null) {
                    Uri contentURI = data.getData();//the content itself
                    uploadImageToStorage(contentURI);//Uploading Image from Gallery to FIREBASE.
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

    protected void uploadImageToStorage(Uri contentURI){
        ///////////////UPLOADING IMAGE TO FIREBASE STORAGE/////////////////////
        StorageReference filePath = mStorageRef.child("profileImage").child(userId+"_Profile_"+contentURI.getLastPathSegment());
        filePath.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "UPLOAD - Success In Uploading Gallery Photo",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"UPLOAD - Failed To Upload Gallery Photo",Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                Toast t = Toast.makeText(getApplicationContext(),"Upload is at: "+progress+"%",Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                t.show();
            }
        });
        ///////////////////////////////////////////////////////////////////////
    }





    //////////////// TODO TRY FOR FULL IMAGE CAPTURE.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }







































}
