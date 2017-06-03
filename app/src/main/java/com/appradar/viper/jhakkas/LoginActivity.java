package com.appradar.viper.jhakkas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;

import models.User;
import utils.MainApplication;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
    int GOOGLE_SIGN_IN = 4324;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    CallbackManager mCallbackManager;
    GoogleLogin googleLogin;
    ProgressDialog nDialog;

    EditText et_email, et_password;
    Button btn_login, btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitialiseFirebaseAuthObjects();
        setContentView(R.layout.activity_login);
        HandleFbLogin();
        InitViews();
    }

    private void InitViews()
    {
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signUp);

        SignInButton signInButton = (SignInButton) findViewById(R.id.btn_google_login);
        googleLogin = new GoogleLogin(LoginActivity.this);

        LoginSignUpBtnClickListener listener = new LoginSignUpBtnClickListener();
        btn_login.setOnClickListener(listener);
        btn_signup.setOnClickListener(listener);
        signInButton.setOnClickListener(listener);
    }

    private void InitialiseFirebaseAuthObjects()
    {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    HideLoading();
                    StartMainActivity();
                    Log.e("InitFirebAuthObs", "onAuthStateChanged: called user not null");
                } else
                {
                    Log.e("onAuthStateChanged", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void HandleFbLogin()
    {
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.btn_fb_login);
        fbLoginButton.setReadPermissions("email", "public_profile");

        mCallbackManager = CallbackManager.Factory.create();

        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("FacebookLogin", "facebook:onSuccess:" + loginResult);
                ShowLoading();
                HandleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("FacebookLogin", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FacebookLogin", "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                HandleGoogleSignInResult(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        else
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void HandleGoogleSignInResult(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        SaveUserToDatabase(credential);

    }

    private void HandleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        SaveUserToDatabase(credential);
    }

    private void SaveUserToDatabase(AuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        HideLoading();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "User with Email id already exists",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            }
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

    public class LoginSignUpBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        {
            if (btn.getId() == R.id.btn_google_login)
            {
                googleLogin.SignIn();
                return;
            }

            String email, password;
            email = et_email.getText().toString();
            password = et_password.getText().toString();
            if (email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(LoginActivity.this, "Please input valid credentials", Toast.LENGTH_LONG).show();
                return;
            }

            ShowLoading();

            if (btn.getId() == R.id.btn_signUp)
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                HideLoading();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
            else if (btn.getId() == R.id.btn_login)
            {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                HideLoading();
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        }
    }


    private class GoogleLogin implements GoogleApiClient.OnConnectionFailedListener
    {
        Context context;
        GoogleApiClient mGoogleApiClient;

        public GoogleLogin(Context context)
        {
            this.context = context;
            HandleGoogleLogin();
        }

        private void HandleGoogleLogin()
        {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage((AppCompatActivity)context /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        public void SignIn() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        }


        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }

}
