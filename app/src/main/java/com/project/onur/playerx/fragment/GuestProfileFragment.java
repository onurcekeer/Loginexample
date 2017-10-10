package com.project.onur.playerx.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.project.onur.playerx.Event;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class GuestProfileFragment extends Fragment{

    User user;
    Event event;
    SQLiteUser sqLiteUser;


    public GuestProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());

        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
        Log.e("user",user.toString());

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("EVENT");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_profile, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        CollapsingToolbarLayout mCollapsingtoolbar = v.findViewById(R.id.toolbar_layout);
        mCollapsingtoolbar.setTitle(event.getUsername());


        CircleImageView profilImage = v.findViewById(R.id.profile_image);

        Picasso.with(v.getContext())
                .load(event.getProfileURL())
                .resize(90, 90)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(profilImage);


        mCollapsingtoolbar.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChatFragment();
            }
        });

    }



    private void startChatFragment(){
        /*
        Fragment fragment = new SettingsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_out_right, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        */
    }


}
