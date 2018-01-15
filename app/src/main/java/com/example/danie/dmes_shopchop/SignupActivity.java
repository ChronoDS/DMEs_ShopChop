
package com.example.danie.dmes_shopchop;

import android.accounts.Account;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;


public class SignupActivity extends AppCompatActivity {

    private TextView etName;        // full name
    private TextView etMail;        // mail
    private TextView etPWD;         // password
    private TextView etCPWD;        // confirm password
    private TextView etPhone;

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

        /** initializing */
        etName = findViewById(R.id.etxt_FullName);
        etMail = findViewById(R.id.etxt_Email);
        etPWD = findViewById(R.id.etxt_Pwrd);
        etCPWD = findViewById(R.id.etxt_CPwrd);
        etPhone = findViewById(R.id.etxt_Phone);

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




    public void onClickSignUp(View view) {
        final String name = etName.getText().toString().trim();
        final String email = etMail.getText().toString().trim();
        final String password = etPWD.getText().toString().trim();
        final String confirmPassword = etCPWD.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        etName.setError(null);
        etMail.setError(null);
        etPWD.setError(null);
        etCPWD.setError(null);
        etPhone.setError(null);

        if(!name.isEmpty()){
             if(isEmailValid(email)){
                 if(isPasswordValid(password) && isPasswordValid(confirmPassword) && password.equals(confirmPassword)){
                     /** do register */
                     FirebaseAccess.getAuthInstance().createUserWithEmailAndPassword(email, password)
                             .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                             if (!task.isSuccessful()) {
                                 Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                         Toast.LENGTH_SHORT).show();
                             } else {
                                 FirebaseAccess.getAuthInstance().signInWithEmailAndPassword(email, password);
                                 User user = new User(name,  phone,  email);
                                 FirebaseAccess.getDatabaseReference().child("users").child(FirebaseAccess.getUserInstance().getUid()).setValue(user);

                                 gotoCategories();

                             }
                         }
                     });

                 }else{     // password is not valid
                     etCPWD.setError("Password is not valid!");
                 }
             }else{         // if mail is not vaild
                 etMail.setError("Email is not valid!");
             }
        }else{          // if name not valid
            etName.setError("Name is not valid!");
        }
    }

    private void gotoCategories(){
        Intent intent = new Intent(this,CategoriesActivity.class);
        startActivity(intent);
        finish();
    }

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



    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }
}
