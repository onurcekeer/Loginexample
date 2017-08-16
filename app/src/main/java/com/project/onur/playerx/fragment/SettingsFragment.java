package com.project.onur.playerx.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;
import com.project.onur.playerx.activity.LoginActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {


    User user;
    SQLiteUser sqLiteUser;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    String new_fullname;
    String new_Url;
    int new_range;


    //UI components
    CircleImageView image_profile;
    FloatingActionButton fab;
    EditText edit_fullname;
    RangeBar rangeBar;
    TextView text_change_password;
    Button button_save_changes;
    Button button_logout;



    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (!(user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com"))) {
                System.out.println("User is signed in with Email");
            }

        }

        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
        Log.e("user",user.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        image_profile = (CircleImageView)v.findViewById(R.id.profile_image);
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        edit_fullname = (EditText)v.findViewById(R.id.fullname);
        rangeBar = (RangeBar)v.findViewById(R.id.rangebar);
        text_change_password = (TextView)v.findViewById(R.id.text_change_pass);
        button_save_changes = (Button)v.findViewById(R.id.save_changes_button);
        button_logout = (Button)v.findViewById(R.id.logout);




        edit_fullname.setText(user.getUsername());
        rangeBar.setSeekPinByValue(user.getRange());




        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        Picasso.with(v.getContext())
                .load(user.getProfilURL())
                .resize(110, 110)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(image_profile);




        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(getString(R.string.string_settings));


    }

    private void logoutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getActivity(),LoginActivity.class);
        startActivity(intent);
    }





/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                //backPreviousFragment();
                getFragmentManager().popBackStack();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backPreviousFragment(){
        Fragment fragment = new ThreeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void onBackPressed()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }
*/
}
