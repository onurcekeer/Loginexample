package com.project.onur.playerx.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by onur on 17.9.2017 at 14:39.
 */

public class ChangePasswordFragment extends android.support.v4.app.Fragment{


    FirebaseUser mUser;
    DatabaseReference mDatabase;
    User user;
    SQLiteUser sqLiteUser;
    EditText edit_email,edit_currentPassword,edit_new_password;
    Button button_change_pass;
    String currentPassword,new_password,email;
    View view;
    AlertDialog dialog;





    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_change_password, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        edit_email = (EditText) v.findViewById(R.id.email);
        edit_currentPassword = (EditText)v.findViewById(R.id.password);
        edit_new_password = (EditText)v.findViewById(R.id.new_password);

        button_change_pass = (Button)v.findViewById(R.id.change_password_button);

        edit_email.setText(user.getEmail());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.success_dialog);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok,null);
        dialog = builder.create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button_positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button_positive.getLayoutParams();
                positiveButtonLL.gravity = Gravity.CENTER;
                button_positive.setLayoutParams(positiveButtonLL);
                button_positive.setTextSize(20);
                button_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                });
            }
        });

        button_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline())
                {
                    View focusView = null;
                    new_password = edit_new_password.getText().toString();
                    currentPassword = edit_currentPassword.getText().toString();
                    email = edit_email.getText().toString();

                    if(!user.getEmail().equals(email)){
                        changeEmail(email);
                    }

                    if(!new_password.equals(user.getPassword())){
                        if(isPasswordValid(new_password) && currentPassword.equals(user.getPassword())){
                            changePassword(new_password);
                        }
                    }


                    if(!TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(new_password)){
                        if(!isPasswordValid(new_password)){
                            focusView = edit_new_password;
                            edit_new_password.setError(getString(R.string.error_invalid_password));
                        }
                        if(!currentPassword.equals(user.getPassword())){
                            focusView = edit_currentPassword;
                            edit_currentPassword.setError(getString(R.string.current_pass_incorrect));
                        }
                        if(new_password.equals(user.getPassword())){
                            focusView = edit_new_password;
                            edit_new_password.setError(getString(R.string.same_password));
                        }
                        if(focusView!=null){
                            focusView.requestFocus();
                        }
                    }
                }
                else{
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.check_internet_conn), Snackbar.LENGTH_LONG);
                    snackbar.show();

                }



            }
        });



        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(getString(R.string.change_pass_toolbar_tittle));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

    }

    public void changeEmail(final String email){
        mUser.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.setEmail(email);
                            mDatabase.child("User").child(user.getUserID()).setValue(user);
                            sqLiteUser.setEmail(user.getUserID(),email);

                            if (!dialog.isShowing()) {
                                dialog.show();
                            }

                        }
                        else{
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.change_email_exeption), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }

    public void changePassword(final String newPassword){

        mUser.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.setPassword(newPassword);
                            mDatabase.child("User").child(user.getUserID()).setValue(user);
                            sqLiteUser.setPassword(user.getUserID(),newPassword);
                            if (!dialog.isShowing()) {
                                dialog.show();
                            }
                        }
                        else{
                            Snackbar snackbar = Snackbar.make(view, getString(R.string.change_email_exeption), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
