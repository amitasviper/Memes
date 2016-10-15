package com.appradar.viper.jhakkas;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import models.TextPost;
import utils.MainApplication;
import utils.UploadPicture;

public class SubmitImage extends AppCompatActivity {

    ImageView iv_content;
    Button btn_submit, btn_select;
    ProgressBar progressBar;

    private static final int IMAGE_REQUEST_CODE = 12341;

    private Uri image_uri;

    StorageReference storageReference;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_image);

        storage = FirebaseStorage.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = storage.getReferenceFromUrl(MainApplication.FIREBASE_STORAGE_URL).child(userId);

        iv_content = (ImageView) findViewById(R.id.iv_content);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_select = (Button) findViewById(R.id.btn_select);
        progressBar = (ProgressBar) findViewById(R.id.pb_image);

        ButtonHandler btnHandler = new ButtonHandler();

        btn_select.setOnClickListener(btnHandler);
        btn_submit.setOnClickListener(btnHandler);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();

                UploadPicture picture = new UploadPicture(selectedImageUri, this.getContentResolver());

                try {
                    image_uri = selectedImageUri;
                    iv_content.setImageBitmap(picture.getBitmap());
                    btn_submit.setEnabled(true);
                    progressBar.setProgress(0);
                } catch (IOException e) {
                    btn_submit.setEnabled(false);
                    e.printStackTrace();
                }

                //image_path = getPath(selectedImageUri);
            }
        }
    }


    public String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private class ButtonHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_select)
            {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), IMAGE_REQUEST_CODE);

                return;
            }

            if (v.getId() == R.id.btn_submit)
            {

                if (image_uri == null)
                {
                    return;
                }

                String filename = UUID.randomUUID().toString();

                UploadTask uploadTask = storageReference.child("images").child(filename).putFile(image_uri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubmitImage.this, "Failed to upload file. Try again", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Calendar c = Calendar.getInstance();

                        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
                        String formattedDate = df.format(c.getTime());

                        String content = "This is a new image";
                        TextPost textPost = new TextPost(0, formattedDate, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(), content, taskSnapshot.getDownloadUrl().toString());
                        MainApplication.FIREBASE_REF.child("posts").push().setValue(textPost);
                        Toast.makeText(SubmitImage.this, "Successfully uploaded image", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                        int currentprogress = (int) progress;
                        progressBar.setProgress(currentprogress);
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                });

                return;
            }

        }
    }
}
