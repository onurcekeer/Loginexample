package com.project.onur.playerx.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.project.onur.playerx.R;
import com.project.onur.playerx.activity.LoginActivity;
import com.project.onur.playerx.activity.MainActivity;
import com.project.onur.playerx.activity.SignUpActivity;


public class ThreeFragment extends Fragment{

    View view;

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
        view = inflater.inflate(R.layout.fragment_three, container, false);
        perform(view);
        return view;
    }

    public void perform(View v) {


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startLoginActivity();
            }
        });


    }


    public void startLoginActivity(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);

    }




}
