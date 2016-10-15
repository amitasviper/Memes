package com.appradar.viper.jhakkas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import models.TextPost;
import utils.MainApplication;
import utils.UploadPicture;

public class SubmitJoke extends AppCompatActivity {

    EditText et_content;
    Button btn_submit;
    ImageView iv_gallery, iv_camera, iv_user_pic, iv_content_image;
    TextView tv_username, tv_reputations;

    static final int IMAGE_REQUEST_CODE = 12341;
    static final int CAMERA_REQUEST_CODE = 34241;
    Uri image_uri;

    StorageReference storageReference;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_joke);

        InitViews();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Public Post");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void InitViews()
    {
        storage = FirebaseStorage.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = storage.getReferenceFromUrl(MainApplication.FIREBASE_STORAGE_URL).child(userId);

        et_content = (EditText) findViewById(R.id.et_content);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        iv_gallery = (ImageView) findViewById(R.id.iv_gallery);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_user_pic = (ImageView) findViewById(R.id.iv_user_pic);
        iv_content_image = (ImageView) findViewById(R.id.iv_content_image);

        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_reputations = (TextView) findViewById(R.id.tv_reputation);

        Picasso.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.default_image).into(iv_user_pic);
        tv_username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        tv_reputations.setText("125");


        ViewClickListener clickListener = new ViewClickListener();

        btn_submit.setOnClickListener(clickListener);
        iv_gallery.setOnClickListener(clickListener);
        iv_camera.setOnClickListener(clickListener);

    }

    private class ViewClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btn_submit:
                    final String content = et_content.getText().toString().trim();
                    if (content.isEmpty() && image_uri == null)
                    {
                        Toast.makeText(SubmitJoke.this, "Please add some content.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (image_uri == null)
                    {
                        Calendar c = Calendar.getInstance();

                        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
                        String formattedDate = df.format(c.getTime());

                        TextPost textPost = new TextPost(0, formattedDate, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(), content, "");

                        MainApplication.FIREBASE_REF.child("posts").push().setValue(textPost);
                        et_content.setText("");

                        Toast.makeText(SubmitJoke.this, "Post submitted for review", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String filename = UUID.randomUUID().toString();

                    UploadTask uploadTask = storageReference.child("images").child(filename).putFile(image_uri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SubmitJoke.this, "Failed to upload file. Try again", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Calendar c = Calendar.getInstance();

                            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
                            String formattedDate = df.format(c.getTime());

                            TextPost textPost = new TextPost(0, formattedDate, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(), content, taskSnapshot.getDownloadUrl().toString());
                            MainApplication.FIREBASE_REF.child("posts").push().setValue(textPost);
                            Toast.makeText(SubmitJoke.this, "Successfully uploaded image", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            System.out.println("Upload is " + progress + "% done");
                            int currentprogress = (int) progress;
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    });

                    break;

                case R.id.iv_gallery:
                    Toast.makeText(SubmitJoke.this, "Gallery Selected", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), IMAGE_REQUEST_CODE);

                    break;


                case  R.id.iv_camera:
                    Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CAMERA_REQUEST_CODE);

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();

                UploadPicture picture = new UploadPicture(selectedImageUri, this.getContentResolver());

                try {
                    image_uri = selectedImageUri;
                    iv_content_image.setVisibility(View.VISIBLE);
                    iv_content_image.setImageBitmap(picture.getBitmap());
                    btn_submit.setEnabled(true);
                } catch (IOException e) {
                    btn_submit.setEnabled(false);
                    e.printStackTrace();
                }

                //image_path = getPath(selectedImageUri);
                return;
            }

            if (requestCode == CAMERA_REQUEST_CODE)
            {
                Uri selectedImageUri = data.getData();

                UploadPicture picture = new UploadPicture(selectedImageUri, this.getContentResolver());

                try {
                    image_uri = selectedImageUri;
                    iv_content_image.setVisibility(View.VISIBLE);
                    iv_content_image.setImageBitmap(picture.getBitmap());
                    btn_submit.setEnabled(true);
                } catch (IOException e) {
                    btn_submit.setEnabled(false);
                    e.printStackTrace();
                }

                //image_path = getPath(selectedImageUri);
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Toast.makeText(SubmitJoke.this, "Back button clicked", Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
