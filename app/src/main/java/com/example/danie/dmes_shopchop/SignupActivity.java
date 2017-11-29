package com.example.danie.dmes_shopchop;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.CAMERA;

public class SignupActivity extends AppCompatActivity {

    private Uri mImageCaptureUri;
    private ImageView mImageView;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
            /**
     * Id to identity CAMERA permission request.
            */
    private static final int REQUEST_CAMERA = 0;
    private EditText etxt_usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etxt_usr = (EditText) findViewById(R.id.etxt_usr);

        final String [] imageOptions = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,imageOptions);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);
                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    dialogInterface.cancel();
                }
                else{
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);

                }
            }
        });

        final AlertDialog dialog = builder.create();

        mImageView = (ImageView)findViewById(R.id.img_btn_picker);

        (findViewById(R.id.img_btn_picker)).setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                dialog.show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK) return;

        Bitmap bitmap = null;
        String path = "";

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri);

            if(path == null)
                path = mImageCaptureUri.getPath();
            if(path != null)
                bitmap = BitmapFactory.decodeFile(path);
        }
        else{
            path = mImageCaptureUri.getPath();
            bitmap = BitmapFactory.decodeFile(path);
        }

        mImageView.setImageBitmap(bitmap);
    }

    public String getRealPathFromURI(Uri contentUri){
        String res = null;
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    protected void notImplementedYet(View view){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context,"Not Implemented Yet!",Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void sendRegistrationForm(View view){

    }


    private void populateAutoComplete() {
        if (!mayRequestCamera()) {
            return;
        }
    }


    private boolean mayRequestCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if(checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if(shouldShowRequestPermissionRationale(CAMERA)){
            Snackbar.make(etxt_usr, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                        }
                    });
        } else {
            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
}

//class RegistrationPacket(String usr, ){
//
//        }