package com.appradar.viper.jhakkas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import models.ChatMessage;
import models.TextPost;
import models.User;
import utils.CipherUtils;
import utils.CircleTransform;

public class NewChatActivity extends AppCompatActivity {
    SlyceMessagingFragment slyceMessagingFragment;
    String unique_id;
    User local_user, friend;
    ChatMessage lastMessage;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        initChatFragment();
        initFriendProfilePic();
        populateMessages();
    }

    private void initFriendProfilePic() {
        Intent intent = getIntent();
        friend = intent.getParcelableExtra("friend");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(friend.getName());
        Picasso.with(this)
                .load(friend.getProfile_pic_url())
                .placeholder(R.drawable.default_image)
                .transform(new CircleTransform())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        actionBar.setIcon(d);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void initChatFragment() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl(MainApplication.FIREBASE_STORAGE_URL).child(userId);

        local_user = new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail());
        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl(local_user.getProfile_pic_url());
        slyceMessagingFragment.setDefaultDisplayName(local_user.getName());
        slyceMessagingFragment.setDefaultUserId("uhtnaeohnuoenhaeuonthhntouaetnheuontheuo");
    }

    private void populateMessages() {

        final String your_email = local_user.getEmail();
        final String friend_email = friend.getEmail();
        unique_id = calculateUniqueId(your_email, friend_email);
        final String uId = unique_id;

        MainActivity.ref.child("chat").child(uId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);

                if (message != null) {
                    if (message.type == 0) {
                        TextMessage textMessage = new TextMessage();
                        textMessage.setText(message.getBody());
                        textMessage.setUserId("LP");
                        textMessage.setDate(new Date().getTime());
                        if (message.getSender().equalsIgnoreCase(your_email)) {
                            textMessage.setDisplayName(local_user.getName());
                            textMessage.setAvatarUrl(local_user.getProfile_pic_url());
                            textMessage.setSource(MessageSource.LOCAL_USER);
                        } else {
                            textMessage.setDisplayName(friend.getName());
                            textMessage.setAvatarUrl(friend.getProfile_pic_url());
                            textMessage.setSource(MessageSource.EXTERNAL_USER);
                        }
                        if (!message.equals(lastMessage)) {
                            slyceMessagingFragment.addNewMessage(textMessage);
                        }
                    }
                    if (message.type == 1) {
                        MediaMessage mediaMessage = new MediaMessage();
                        mediaMessage.setUrl(message.getBody());
                        mediaMessage.setUserId("LP");
                        mediaMessage.setDate(new Date().getTime());
                        if (message.getSender().equalsIgnoreCase(your_email)) {
                            mediaMessage.setDisplayName(local_user.getName());
                            mediaMessage.setAvatarUrl(local_user.getProfile_pic_url());
                            mediaMessage.setSource(MessageSource.LOCAL_USER);
                        } else {
                            mediaMessage.setDisplayName(friend.getName());
                            mediaMessage.setAvatarUrl(friend.getProfile_pic_url());
                            mediaMessage.setSource(MessageSource.EXTERNAL_USER);
                        }
                        if (!message.equals(lastMessage))
                            slyceMessagingFragment.addNewMessage(mediaMessage);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //chatMessages.clear();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        slyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {
            @Override
            public void onUserSendsTextMessage(String text) {
                lastMessage = new ChatMessage(your_email, friend.getEmail(), text.trim(), "123", ChatMessage.MessageType.TEXT);
                MainActivity.ref.child("chat").child(uId).child("messages").push().setValue(lastMessage);
            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {
                String filename = UUID.randomUUID().toString();
                UploadTask uploadTask = storageReference.child("chat-images").child(filename).putFile(imageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewChatActivity.this, "Failed to upload image. Try again", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        lastMessage = new ChatMessage(your_email, friend.getEmail(), downloadUrl.toString(), "123", ChatMessage.MessageType.IMAGE);
                        MainActivity.ref.child("chat").child(uId).child("messages").push().setValue(lastMessage);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        } else if (menuItem.getItemId() == R.id.delete_chat) {

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(NewChatActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Clear chat history")
                    .setMessage("This will delete complete chat history from both ends. You want to prceed?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.ref.child("chat").child(unique_id).child("messages").removeValue();
                            slyceMessagingFragment.replaceMessages(new ArrayList<Message>());
                            Toast.makeText(NewChatActivity.this, "All messages removed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu_chat, menu);
        return true;
    }

    private String calculateUniqueId(String your_email, String friend_email) {
        long userOne = CipherUtils.GetHashedNumber(your_email);
        long userTwo = CipherUtils.GetHashedNumber(friend_email);

        long resultId = userOne + userTwo;
        return CipherUtils.GetHashedString(Long.toString(resultId));
    }
}
