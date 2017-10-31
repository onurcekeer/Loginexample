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
import android.view.View;

import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.model.User;
import com.project.onur.playerx.fragment.HomeFragment;
import com.project.onur.playerx.R;
import com.project.onur.playerx.fragment.ProfileFragment;
import com.project.onur.playerx.fragment.MessagesFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

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
        userLocation = getCurrentLocation();

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
        final BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.inflateMenu(R.menu.navigation);


        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if(isOpen){
                            bottomNavigation.setVisibility(View.GONE);
                        }
                        else {
                            bottomNavigation.setVisibility(View.VISIBLE);
                        }
                    }
                });


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
                pushFragment(new HomeFragment());
                break;
            case R.id.navigation_messages:
                pushFragment(new MessagesFragment());
                break;
            case R.id.navigation_profile:
                pushFragment(new ProfileFragment());
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

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}