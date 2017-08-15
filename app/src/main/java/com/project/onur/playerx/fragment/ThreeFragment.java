package com.project.onur.playerx.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;
import com.project.onur.playerx.activity.LoginActivity;
import com.project.onur.playerx.activity.MainActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ThreeFragment extends Fragment{

    FirebaseAuth mAuth;
    User user;
    SQLiteUser sqLiteUser;


    public ThreeFragment() {
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


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        mAuth = FirebaseAuth.getInstance();

        CollapsingToolbarLayout mCollapsingtoolbar = (CollapsingToolbarLayout)v.findViewById(R.id.toolbar_layout);
        mCollapsingtoolbar.setTitle(user.getUsername());

        CircleImageView profilImage = (CircleImageView)v.findViewById(R.id.profile_image);

        Picasso.with(v.getContext())
                .load(user.getProfilURL())
                .resize(90, 90)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(profilImage);


        mCollapsingtoolbar.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logoutUser();
                startSettingsFragment();
            }
        });

    }

    private void logoutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getActivity(),LoginActivity.class);
        startActivity(intent);
    }

    private void startSettingsFragment(){
        Fragment fragment = new SettingsFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
