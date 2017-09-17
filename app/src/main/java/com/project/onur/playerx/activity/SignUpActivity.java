package com.project.onur.playerx.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;

import java.io.Serializable;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;

public class SignUpActivity extends AppCompatActivity {


    private static final String KEY_USER = "KEY_USER";
    private static final String TAG = "CREATE_USER";
    private static final int DEFAULT_RANGE = 20;
    private static final String DEFAULT_USER_PROFİLE = "https://firebasestorage.googleapis.com/v0/b/playerx-e6194.appspot.com/o/default_user.png?alt=media&token=ae78ed09-9dfb-4c6d-a261-2aec523d22a0";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    EditText edit_email, edit_password, edit_fullname;
    Button button_register;
    String email,password, fullname;
    ProgressDialog progressDialog;
    View view;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();


        view = findViewById(R.id.sign_up_lineer_layout);

        edit_email = (EditText)findViewById(R.id.email);
        edit_password = (EditText)findViewById(R.id.password);
        edit_fullname = (EditText)findViewById(R.id.fullname);
        button_register = (Button)findViewById(R.id.register);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        setBottomBar();
    }



    private void attemptLogin() {

        // Reset errors.
        edit_email.setError(null);
        edit_password.setError(null);
        edit_fullname.setError(null);

        email = edit_email.getText().toString();
        password = edit_password.getText().toString();
        fullname = edit_fullname.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(fullname) || !isFullnameValid(fullname)) {
            edit_fullname.setError(getString(R.string.error_invalid_fullname));
            focusView = edit_fullname;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            edit_password.setError(getString(R.string.error_invalid_password));
            focusView = edit_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            edit_email.setError(getString(R.string.error_invalid_email));
            focusView = edit_email;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {

            if(!isOnline()){
                Snackbar snackbar = Snackbar.make(view, getString(R.string.check_internet_conn), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else {
                showProgressDialog();
                createUser(email,password,fullname);
            }

        }
    }

    public void createUser(final String email, String password, final String fullname){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            mUser = mAuth.getCurrentUser();

                            addUserDatabase(email,fullname);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.create_user_failed), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    public void addUserDatabase(final String email, final String fullname){


        new Thread(new Runnable() {
            @Override
            public void run() {

                String _userId = mUser.getUid();
                String _profilUrl = DEFAULT_USER_PROFİLE;
                int _range = DEFAULT_RANGE;

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("User");
                user = new User(_userId,email,fullname,_profilUrl,_range);
                reference.child(_userId).setValue(user);

                SQLiteUser sqLiteUser = new SQLiteUser(getApplicationContext());
                sqLiteUser.addUserToSQLite(user);

                progressDialog.dismiss();
                updateUI(user);

            }
        }).start();

    }

    public void showProgressDialog(){

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage(getString(R.string.user_is_being_created));
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isFullnameValid(String fullname) {
        //TODO: Replace this with your own logic
        return fullname.length() > 5;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    public void setBottomBar(){

        // Login clickable
        if(Locale.getDefault().getLanguage().equals("tr"))
        {
            SpannableString ss2 = new SpannableString("Zaten hesabın var mı? Giriş Yap.");
            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = LoginActivity.newIntent(SignUpActivity.this, 1);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss2.setSpan(clickableSpan2, 22, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView textView = (TextView) findViewById(R.id.already_have_an_account);
            textView.setText(ss2);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setLinkTextColor(getResources().getColor(R.color.colorPrimaryDark));
            textView.setHighlightColor(Color.TRANSPARENT);
        }
        else{
            SpannableString ss = new SpannableString("Already have an account? Login.");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = LoginActivity.newIntent(SignUpActivity.this, 1);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            ss.setSpan(clickableSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView textView = (TextView) findViewById(R.id.already_have_an_account);
            textView.setText(ss);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setLinkTextColor(getResources().getColor(R.color.colorPrimaryDark));
            textView.setHighlightColor(Color.TRANSPARENT);
        }

    }

    public static Intent newIntent(Activity callerActivity, int parameter){
        Intent intent=new Intent(callerActivity, SignUpActivity.class);
        intent.putExtra(KEY_USER,parameter);
        return intent;
    }


    public void updateUI(User user){

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("userObject",user);
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI(user);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();


        // ...
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
