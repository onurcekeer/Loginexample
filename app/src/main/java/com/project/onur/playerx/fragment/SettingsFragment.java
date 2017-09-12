package com.project.onur.playerx.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.User;
import com.project.onur.playerx.activity.LoginActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {


    private static final int CAMERA_REQUEST = 1888;
    final int GET_FROM_GALLERY = 54;
    private static final String DEFAULT_USER_PROFİLE = "https://firebasestorage.googleapis.com/v0/b/playerx-e6194.appspot.com/o/default_user.png?alt=media&token=ae78ed09-9dfb-4c6d-a261-2aec523d22a0";


    User user;
    SQLiteUser sqLiteUser;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String new_fullname;
    String new_Url;
    Bitmap bitmap_new,bitmap_previous;
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
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
        Log.e("user",user.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        perform(view);
        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.camera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.select_photo:
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getContext().getPackageManager()) != null)
                    startActivityForResult(intent,GET_FROM_GALLERY);
                break;
            case R.id.remove_photo:
                image_profile.setImageResource(R.drawable.ic_default_user);
                new_Url = DEFAULT_USER_PROFİLE;
                user.setProfilURL(new_Url);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu,menu);

    }

    private void perform(View v) {

        image_profile = (CircleImageView)v.findViewById(R.id.profile_image);
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        edit_fullname = (EditText)v.findViewById(R.id.fullname);
        rangeBar = (RangeBar)v.findViewById(R.id.rangebar);
        text_change_password = (TextView)v.findViewById(R.id.text_change_pass);
        button_save_changes = (Button)v.findViewById(R.id.save_changes_button);
        button_logout = (Button)v.findViewById(R.id.logout);

        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                text_change_password.setVisibility(View.VISIBLE);
                System.out.println("User is signed in with email/password");
            }
        }

        registerForContextMenu(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.performLongClick();
            }
        });

        edit_fullname.setText(user.getUsername());
        rangeBar.setSeekPinByValue(user.getRange());
        image_profile.setDrawingCacheEnabled(true);
        image_profile.buildDrawingCache();
        bitmap_previous = image_profile.getDrawingCache();

        bitmap_new = bitmap_previous;





        button_save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_fullname= edit_fullname.getText().toString();
                if(isFullnameValid(new_fullname)){
                    saveChanges();
                }
                else{
                    edit_fullname.setError(getString(R.string.error_invalid_fullname));
                }
            }
        });
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap_new = (Bitmap) data.getExtras().get("data");
            image_profile.setImageBitmap(bitmap_new);
            if(isOnline()){
                uploadUserPhoto(bitmap_new);
            }
            else{
                // TODO: 12.9.2017 add snackbar 
            }
        }

        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                bitmap_new = BitmapFactory.decodeStream(imageStream);
                image_profile.setImageBitmap(bitmap_new);
                if(isOnline()){
                    uploadUserPhoto(bitmap_new);
                }
                else{
                    // TODO: 12.9.2017 add snackbar
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Toast.makeText(SettingsFragment.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }

    }


    private void uploadUserPhoto(final Bitmap bitmap){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] data = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                UploadTask uploadTask = storageRef.child(user.getUserID()+"jpg").putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // TODO: add snackbar
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        new_Url = downloadUrl.toString();
                        user.setProfilURL(new_Url);
                    }
                });

            }
        }).start();

    }



    public void saveChanges(){


        new_range= Integer.parseInt(rangeBar.getRightPinValue());

        if(isOnline()){

            if(user.getUsername().equals(new_fullname) || (user.getRange()!=new_range) || user.getProfilURL().equals(new_Url)){
                user.setRange(new_range);
                user.setUsername(new_fullname);


                mDatabase.child("User").child(user.getUserID()).setValue(user);
                SQLiteUser sqliteUser = new SQLiteUser(getContext());
                sqliteUser.addUserToSQLite(user);

            }
        }
        else {

            // TODO: 22.8.2017 add snackbar
        }


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isFullnameValid(String fullname) {
        //TODO: Replace this with your own logic
        return fullname.length() > 5;
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
