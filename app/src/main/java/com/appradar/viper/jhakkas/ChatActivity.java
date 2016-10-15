package com.appradar.viper.jhakkas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import customadapters.ChatAdapter;
import models.ChatMessage;
import models.User;
import utils.CipherUtils;
import utils.CircleTransform;

public class ChatActivity extends AppCompatActivity {

    EditText et_message;
    ImageButton btn_send_message;
    ImageView iv_emoji;
    RecyclerView recycler_view_chat;

    String unique_id;

    final private ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    final ChatAdapter adapter = new ChatAdapter(chatMessages);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InitViews();
    }


    private void InitViews()
    {

        Intent intent = getIntent();
        final User friend = intent.getParcelableExtra("friend");


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(friend.getName());
        Picasso.with(this)
                .load(friend.getProfile_pic_url())
                .placeholder(R.drawable.default_image)
                .transform(new CircleTransform())
                .into(new Target()
                {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                    {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        actionBar.setIcon(d);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable)
                    {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable)
                    {
                    }
                });

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_message = (EditText) findViewById(R.id.et_message);
        btn_send_message = (ImageButton) findViewById(R.id.btn_send_message);
        recycler_view_chat = (RecyclerView) findViewById(R.id.recycler_view_chat);

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view_chat.setLayoutManager(mLayoutManager);
        recycler_view_chat.setItemAnimator(new DefaultItemAnimator());

        recycler_view_chat.setAdapter(adapter);

        final String your_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final String friend_name = friend.getName();

        unique_id = calculateUniqueId(your_name, friend_name);
        final String uId =  unique_id;

/*        MainActivity.ref.child("chat").child(uniqueId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String key = postSnapshot.getChildren().iterator().next().getKey();
                    ChatMessage message = postSnapshot.getValue(ChatMessage.class);

                    if (message != null)
                        messageList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/

        MainActivity.ref.child("chat").child(uId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);

                if (message != null)
                    chatMessages.add(message);
                adapter.notifyDataSetChanged();
                recycler_view_chat.scrollToPosition(chatMessages.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                chatMessages.clear();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_text = et_message.getText().toString();
                if (msg_text.trim().isEmpty())
                    return;
                et_message.setText("");

                ChatMessage message = new ChatMessage(your_name, friend_name, msg_text.trim(), "123");
                MainActivity.ref.child("chat").child(uId).child("messages").push().setValue(message);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Toast.makeText(ChatActivity.this, "Home button clicked", Toast.LENGTH_SHORT).show();
            finish();
        }

        else if (menuItem.getItemId() == R.id.delete_chat)
        {
            MainActivity.ref.child("chat").child(unique_id).child("messages").removeValue();
            chatMessages.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(ChatActivity.this, "All messages removed", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu_chat, menu);
        return true;
    }


    private String calculateUniqueId(String your_name, String friend_name)
    {

        long userOne = CipherUtils.GetHashedNumber(your_name);
        long userTwo = CipherUtils.GetHashedNumber(friend_name);

        long resultId = userOne + userTwo;
        return CipherUtils.GetHashedString(Long.toString(resultId));
    }
}

//sample cooment to test git repo