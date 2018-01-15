package com.example.danie.dmes_shopchop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joooonho.SelectableRoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    // Image Capture/Choosing variables:
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_FROM_FILE = 2;
    private static SelectableRoundedImageView RndImg;
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
    Uri uploadDownloadUri;
    StorageReference filePath;
    //

    // full image capture
    String mCurrentPhotoPath;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Create a storage reference from our app
        mStorageRef = FirebaseStorage.getInstance().getReference();
//        downloadImageFromStorage();

        fullname = findViewById(R.id.pName);
        phonenum = findViewById(R.id.pPhone);
        emailadd = findViewById(R.id.pEmail);
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

        RndImg = findViewById(R.id.btnProfilePic);
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
        super.onActivityResult(requestCode,resultCode,data);

        Bitmap bitmap;
        String path = "";

        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                RndImg.setImageBitmap(imageBitmap);

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
        user = FirebaseAccess.getUserInstance();
        if (user != null) {
            // Name, email address, and profile photo Url

            String name = user.getDisplayName();
            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();

            // Initialize Fields for profile:

            dbRef.child("users").child(user.getUid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    downloadProfileImageFromDB();
                    Map<String, Object> hashMap = (Map<String, Object>) dataSnapshot.getValue();
                    Set<String> keys = hashMap.keySet();
                    for(String key : keys){
                        switch(key) {
                            case "name":
                                fullname.setText(hashMap.get(key).toString());
                                break;
                            case "phone":
                                phonenum.setText(hashMap.get(key).toString());
                                break;
                            case "email" :
                                emailadd.setText(hashMap.get(key).toString());
                                break;
                            default:
                                break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "failed to get user data", Toast.LENGTH_SHORT).show();
                }
            });
            ////////////////////////////////////
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
    ///////////////UPLOADING IMAGE TO FIREBASE STORAGE/////////////////////
    protected void uploadImageToStorage(Uri contentURI){
        filePath = mStorageRef.child("profileImage").child(user.getUid() + "_Profile_");
        filePath.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "UPLOAD - Success In Uploading Gallery Photo",Toast.LENGTH_LONG).show();
                uploadDownloadUri = taskSnapshot.getDownloadUrl();
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
    }


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


    ///////////////DOWNLOADING IMAGE FROM FIREBASE STORAGE/////////////////////
    protected boolean downloadProfileImageFromDB(){
        RndImg = (SelectableRoundedImageView) findViewById(R.id.btnProfilePic);
        if(user!=null) {
            filePath = mStorageRef.child("profileImage").child(user.getUid()+"_Profile_");
            if (filePath != null) {
                Toast.makeText(getApplicationContext(), "Loading Profile Image", Toast.LENGTH_SHORT).show();
                Glide.with(this).using(new FirebaseImageLoader()).load(filePath).into(RndImg);

                return true;
            } else {
                Toast.makeText(getApplicationContext(), "filePath is null", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else{
                Toast.makeText(getApplicationContext(), "filePath is null", Toast.LENGTH_LONG).show();
                return false;
        }
    }
}
