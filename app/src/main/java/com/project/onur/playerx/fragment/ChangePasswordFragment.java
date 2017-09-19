package com.project.onur.playerx.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;

/**
 * Created by onur on 17.9.2017 at 14:39.
 */

public class ChangePasswordFragment extends android.support.v4.app.Fragment{


    FirebaseUser mUser;
    User user;
    SQLiteUser sqLiteUser;
    EditText edit_email,edit_currentPassword,edit_new_password;
    Button button_change_pass;
    String currentPassword,new_password,email;
    View view;





    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());

        mUser = FirebaseAuth.getInstance().getCurrentUser();
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
                            showInfoDialog();
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
                            Toast.makeText(getContext(),"User email address updated.",Toast.LENGTH_SHORT).show();
                            sqLiteUser.setEmail(user.getUserID(),email);
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
                            Toast.makeText(getContext(),"User password updated",Toast.LENGTH_SHORT).show();
                            sqLiteUser.setPassword(user.getUserID(),newPassword);
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


    public void showInfoDialog(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
