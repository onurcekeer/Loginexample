package com.project.onur.playerx.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;

import java.util.Arrays;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;


public class LoginActivity extends AppCompatActivity{

    private static final String KEY_USER = "KEY_USER";
    private static final String TAG = "LOGİN";
    private static final int RC_SIGN_IN = 9001;
    private static final int DEFAULT_RANGE = 20;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager mCallbackManager;
    User user;
    SQLiteUser sqliteUser;
    double latitude,longitude;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    String email,password;
    View view;
    View googleButton;
    View facebookButton;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        view = findViewById(R.id.login_lineer_layout);
        googleButton = findViewById(R.id.linearButton2);
        facebookButton = findViewById(R.id.linearButton);
        mAuth = FirebaseAuth.getInstance();

        user = new User();
        sqliteUser = new SQLiteUser(getApplicationContext());

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
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


        facebookButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));                }
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



        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                showProgressDialog();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Snackbar snackbar = Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        setBottomBar();

    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mUser = mAuth.getCurrentUser();
                            getUserFromFirebase(mUser);
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }


                    }
                });
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
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                showProgressDialog();
            }
            else {
                //hata.show();
            }
        }

        if(requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            getUserFromFirebase(mUser);
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }


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
        Cursor cursor = sqliteUser.query();
        user = sqliteUser.getUserFromSQLite(cursor);

        if(currentUser!=null && user!=null){
            startMainActivity(user);
        }
    }

    @Override
    public void onStop(){
        super.onStop();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    public void startMainActivity(User user){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("userObject",user);
        startActivity(i);
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
                            getUserFromFirebase(mUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.user_auth_failed), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                        // ...
                    }
                });


    }

    public void getUserFromFirebase(final FirebaseUser _mUser){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        final User[] user1 = {new User()};
        Query query = reference.orderByChild("userID").equalTo(_mUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   User _user = dataSnapshot.child(_mUser.getUid()).getValue(User.class);
                    user1[0] = _user;
                }
                else {
                    user1[0]= collectUserData(_mUser);
                    addUserDataToFirebase(user1[0]);
                }
                sqliteUser.addUserToSQLite(user1[0]);
                startMainActivity(user1[0]);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //hata mesajı basılacak.
            }
        });

        progressDialog.dismiss();
    }

    public User collectUserData(FirebaseUser _mUser){

        User _user = new User();

        _user.setUserID(_mUser.getUid());
        _user.setEmail(_mUser.getEmail());
        _user.setUsername(_mUser.getDisplayName());
        _user.setProfilURL(_mUser.getPhotoUrl().toString());
        _user.setRange(DEFAULT_RANGE);

        return _user;
    }

    public void addUserDataToFirebase(User _user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("User");
        reference.child(_user.getUserID()).setValue(_user);

    }
}

