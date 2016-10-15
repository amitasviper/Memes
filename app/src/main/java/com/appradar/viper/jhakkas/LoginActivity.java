package com.appradar.viper.jhakkas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import models.User;
import utils.MainApplication;

public class LoginActivity extends AppCompatActivity {

    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    ImageView iv_user_pic;
    String TAG = "LoginActivity";

    ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    HideLoading();
                    StartMainActivity();
                } else
                {
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                }
            }
        };

        setContentView(R.layout.activity_login);

        iv_user_pic = (ImageView) findViewById(R.id.iv_user_pic);

        LoginButton loginButton = (LoginButton) findViewById(R.id.btn_fb_login);
        loginButton.setReadPermissions("email", "public_profile");

        mCallbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("FacebookLogin", "facebook:onSuccess:" + loginResult);
                ShowLoading();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FacebookLogin", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookLogin", "facebook:onError", error);
            }
        });



        Button next = (Button) findViewById(R.id.btn_next_activity);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartMainActivity();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            FirebaseUser user_info = FirebaseAuth.getInstance().getCurrentUser();
                            if (user_info != null)
                            {
                                User userdetails = new User(user_info.getDisplayName(), user_info.getPhotoUrl().toString(), user_info.getEmail());

                                Firebase myRef = new Firebase(MainApplication.FIREBASE_URL);

                                myRef.child("users").child(user_info.getUid()).setValue(userdetails);

                                Toast.makeText(LoginActivity.this, "New user saved", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // ...
                    }
                });
    }

    private void StartMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void ShowLoading()
    {
        if(nDialog == null)
            Loading();
        nDialog.show();
    }

    private void HideLoading()
    {
        if(nDialog == null)
            return;
        nDialog.hide();
    }
    private void Loading()
    {
        nDialog = new ProgressDialog(LoginActivity.this);
        nDialog.setMessage("Please wait");
        nDialog.setTitle("Signing in");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
    }


}
