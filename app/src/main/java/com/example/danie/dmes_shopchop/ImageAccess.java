package com.example.danie.dmes_shopchop;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;


// THIS ALL CLASS IS PROBLEMATIC..
// FIRST BECAUSE OF HOW DO I REFER AN ACTIVITY IN FUNCTION.
// SECOND "onActivityResult" is started from an external class..hence i cannot ask it to receive another variable.
/*
public class ImageAccess extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    protected void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


// THIS function cannot be changed because its operated out of system functions..meaning i cannot add variable ImgBtn to it..
    protected void onActivityResult(int requestCode, int resultCode, Intent data, ImageButton ImgBtn) {
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
                        Toast.makeText(act.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                        ImgBtn.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SignupActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }
    }

}
*/