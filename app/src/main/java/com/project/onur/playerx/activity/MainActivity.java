package com.project.onur.playerx.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.SharedPrefUtil;
import com.project.onur.playerx.model.User;
import com.project.onur.playerx.fragment.HomeFragment;
import com.project.onur.playerx.R;
import com.project.onur.playerx.fragment.ProfileFragment;
import com.project.onur.playerx.fragment.MessagesFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import im.delight.android.location.SimpleLocation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {


    SimpleLocation mLocation;
    double latitude,longitude;
    String userLocation;
    SQLiteUser sqLiteUser;
    String LocationPermission;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityPermissionsDispatcher.checkLocationPermissionWithPermissionCheck(this);

        LocationPermission = (Manifest.permission.ACCESS_FINE_LOCATION);
        sqLiteUser = new SQLiteUser(getApplicationContext());

        if(getApplicationContext().checkCallingOrSelfPermission(LocationPermission) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("userObject");

        if(user.getUserID()==null){
            Cursor cursor = sqLiteUser.query();
            user = sqLiteUser.getUserFromSQLite(cursor);
            Log.e("user",user.toString());
        }

        replaceFcmToken();
        setupNavigationView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(getApplicationContext().checkCallingOrSelfPermission(LocationPermission) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void checkLocationPermission(){

    }

    public void getCurrentLocation(){
        String location;
        mLocation = new SimpleLocation(this);
        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();
        location = latitude+","+longitude;
        mLocation.beginUpdates();
        userLocation = location;
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        Toast.makeText(this, "izin reddedildi", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocation() {
        Toast.makeText(this, "Konum izni bi daha sorma olarak işaretli. Ayarlardan Konuma izin verebilirsiniz.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void replaceFcmToken(){
        String currentFcmToken = new SharedPrefUtil(getApplicationContext()).getString("fcmToken");
        if(!user.getFcmToken().equals(currentFcmToken)){

            user.setFcmToken(currentFcmToken);
            sqLiteUser.setFcmtoken(user.getUserID(),currentFcmToken);
            FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("fcmToken")
                    .setValue(currentFcmToken);

        }

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivityPermissionsDispatcher.checkLocationPermissionWithPermissionCheck(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(getApplicationContext().checkCallingOrSelfPermission(LocationPermission) == PackageManager.PERMISSION_GRANTED && mLocation!=null){
            mLocation.endUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getApplicationContext().checkCallingOrSelfPermission(LocationPermission) == PackageManager.PERMISSION_GRANTED && mLocation!=null){
            mLocation.endUpdates();
        }
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