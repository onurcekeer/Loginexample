package com.project.onur.playerx.activity;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;
import com.project.onur.playerx.fragment.OneFragment;
import com.project.onur.playerx.R;
import com.project.onur.playerx.fragment.ThreeFragment;
import com.project.onur.playerx.fragment.TwoFragment;

import im.delight.android.location.SimpleLocation;


public class MainActivity extends AppCompatActivity {


    SimpleLocation mLocation;
    double latitude,longitude;
    String userLocation;
    SQLiteUser sqLiteUser;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = new SimpleLocation(this);
        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();
        userLocation = latitude+","+longitude;

        sqLiteUser = new SQLiteUser(getApplicationContext());

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("userObject");

        if(user==null){
            Cursor cursor = sqLiteUser.query();
            user = sqLiteUser.getUserFromSQLite(cursor);
            Log.e("user",user.toString());
        }


        setupNavigationView();
    }




    private void setupNavigationView() {
        BottomNavigationView bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.inflateMenu(R.menu.navigation);
        if (bottomNavigation != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigation.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            bottomNavigation.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }




    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.navigation_home:
                pushFragment(new OneFragment());
                break;
            case R.id.navigation_dashboard:
                pushFragment(new TwoFragment());
                break;
            case R.id.navigation_notifications:
                pushFragment(new ThreeFragment());
                break;
            }
    }


    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.main_container, fragment);
                ft.commit();
            }
        }
    }

    private String getCurrentLocation(){
        String location;
        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();
        location = latitude+","+longitude;
        return location;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mLocation.beginUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocation.endUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocation.endUpdates();
    }


}