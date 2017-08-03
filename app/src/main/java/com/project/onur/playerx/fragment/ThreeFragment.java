package com.project.onur.playerx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.project.onur.playerx.R;
import com.project.onur.playerx.activity.LoginActivity;


public class ThreeFragment extends Fragment{

    FirebaseAuth mAuth;


    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        mAuth = FirebaseAuth.getInstance();
        Button button_logout = (Button)v.findViewById(R.id.button_logout);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });


    }

    private void logoutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getActivity(),LoginActivity.class);
        startActivity(intent);
    }



}
