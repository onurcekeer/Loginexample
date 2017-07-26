package com.project.onur.playerx.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.project.onur.playerx.R;
import java.util.Locale;



public class LoginActivity extends AppCompatActivity{

    private static final String KEY_USER = "KEY_USER";
    private static final String TAG = "LOGİN";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    String email,password;
    View view;
    View googleButton;
    ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        view = findViewById(R.id.login_lineer_layout);
        googleButton = findViewById(R.id.linearButton2);
        mAuth = FirebaseAuth.getInstance();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    signIn();
                }
                else{
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.check_internet_conn), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        setBottomBar();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                showProgressDialog();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mUser = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            startMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.user_auth_failed), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                        // ...
                    }
                });
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if(!isOnline()){
                Snackbar snackbar = Snackbar.make(view, getString(R.string.check_internet_conn), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else {

                showProgressDialog();
                signWithEmailandPassword(email,password);
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }


    public static Intent newIntent(Activity callerActivity, int parameter){
        Intent intent=new Intent(callerActivity, LoginActivity.class);
        intent.putExtra(KEY_USER,parameter);
        return intent;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            startMainActivity();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void startMainActivity(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void setBottomBar(){

        if(Locale.getDefault().getLanguage().equals("tr"))
        {
            SpannableString ss2 = new SpannableString("Hesabın yok mu? Kaydol.");
            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = SignUpActivity.newIntent(LoginActivity.this, 1);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss2.setSpan(clickableSpan2, 16, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView textView = (TextView) findViewById(R.id.dont_have_account_textview);
            textView.setText(ss2);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }
        else{
            SpannableString ss = new SpannableString("Don't have an account? Sign Up.");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = SignUpActivity.newIntent(LoginActivity.this, 1);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            ss.setSpan(clickableSpan, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView textView = (TextView) findViewById(R.id.dont_have_account_textview);
            textView.setText(ss);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showProgressDialog(){

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.logging_in));
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();

    }

    public void signWithEmailandPassword(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mUser = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            startMainActivity();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.user_auth_failed), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });


    }
}

