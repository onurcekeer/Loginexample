package com.project.onur.playerx.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;

/**
 * Created by onur on 17.9.2017 at 14:39.
 */

public class ChangePasswordFragment extends android.support.v4.app.Fragment{


    User user;
    SQLiteUser sqLiteUser;
    EditText edit_email,edit_currentPassword,edit_new_password;
    Button button_change_pass;
    String currentPassword,new_password,email;





    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());

        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_change_password, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        edit_email = (EditText) v.findViewById(R.id.email);
        edit_currentPassword = (EditText)v.findViewById(R.id.password);
        edit_new_password = (EditText)v.findViewById(R.id.new_password);

        button_change_pass = (Button)v.findViewById(R.id.change_password_button);

        edit_email.setText(user.getEmail());














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





}
