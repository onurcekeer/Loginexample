package com.project.onur.playerx.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.onur.playerx.CharacterCountErrorWatcher;
import com.project.onur.playerx.model.Event;
import com.project.onur.playerx.model.ItemData;
import com.project.onur.playerx.model.LatLon;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.adapter.SpinnerAdapter;
import com.project.onur.playerx.model.User;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.location.SimpleLocation;

/**
 * Created by onur on 22.9.2017 at 00:44.
 */

public class MyEventFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{


    private static final String FIRSTMAPACTION = "FIRSTMAPACTION";
    Date selectedDate, nowDate, selectedTime, nowTime, eventDateTime;
    GoogleMap mMap;
    LatLng myLocation;
    LatLon lastLocation;
    Marker marker;
    String title,description;
    Event event;
    User user;
    SQLiteUser sqLiteUser;
    boolean firstMapAction;
    SimpleLocation simpleLocation;

    TextView dateTextView, timeTextView, location_text;
    FloatingActionButton add_marker;
    Dialog dialog;
    TextInputLayout til_title,til_description;
    View view;
    Spinner spinner_category;

    public MyEventFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
        Log.e("user",user.toString());
        simpleLocation = new SimpleLocation(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("EVENT");
        }

        view = inflater.inflate(R.layout.fragment_my_event, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {
        simpleLocation.beginUpdates();
        dateTextView = v.findViewById(R.id.date_text);
        dateTextView.setClickable(true);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        dateTextView.setText(df.format(event.getDateTime()));
        timeTextView = v.findViewById(R.id.time_text);
        timeTextView.setClickable(true);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(event.getDateTime()));
        location_text = v.findViewById(R.id.location_text);
        location_text.setText(String.valueOf(event.getLocation().getLatitude())+" , "+String.valueOf(event.getLocation().getLongitude()));


        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(getString(R.string.edit_event));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleLocation.endUpdates();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData(getString(R.string.sport_text), R.drawable.ic_football_mini));
        list.add(new ItemData(getString(R.string.table_games_text), R.drawable.ic_table_games_mini));
        list.add(new ItemData(getString(R.string.concert_text), R.drawable.ic_concert_mini));
        list.add(new ItemData(getString(R.string.pc_games_text), R.drawable.ic_pc_games_mini));
        list.add(new ItemData(getString(R.string.cinema_text), R.drawable.ic_cinema_mini));
        list.add(new ItemData(getString(R.string.other_text), R.drawable.ic_other_mini));
        spinner_category = v.findViewById(R.id.spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(),
                R.layout.spinner_row, R.id.txt, list);
        spinner_category.setAdapter(adapter);
        spinner_category.setSelection(event.getCategory());

        til_title = v.findViewById(R.id.text_title);
        til_description = v.findViewById(R.id.text_description);


        til_title.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(til_title, 1, 80));
        til_description.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(til_description, 1, 200));
        lastLocation = event.getLocation();
        selectedDate = event.getDateTime();
        selectedTime = event.getDateTime();

        nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        nowDate = calendar.getTime();

        til_title.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable edt) {
                if (til_title.getEditText().getText().length() > 0) {
                    til_title.getEditText().setError(null);
                }
            }
        });

        til_description.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void afterTextChanged(Editable edt) {
                if (til_description.getEditText().getText().length() > 0) {
                    til_description.getEditText().setError(null);
                }
            }
        });

        til_title.getEditText().setText(event.getTitle());
        til_description.getEditText().setText(event.getDescription());

        View add_location = v.findViewById(R.id.add_location_form);
        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapDialog();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime();
            }
        });

        Button create_event_button = v.findViewById(R.id.create_event_button);
        create_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUpdate();
            }
        });

        Button delete_event_button = v.findViewById(R.id.delete_event);
        delete_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.delete_event))
                        .setContentText(getString(R.string.delete_event_question))
                        .setCancelText(getString(android.R.string.cancel))
                        .setConfirmText(getString(android.R.string.yes))
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if(isOnline()){
                                    deleteEvent(event);
                                    sweetAlertDialog.dismiss();
                                    Toast.makeText(getContext(),getString(R.string.event_deleted),Toast.LENGTH_SHORT).show();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    fm.popBackStack();
                                }
                                else {
                                    Snackbar snackbar = Snackbar.make(view, getString(R.string.check_internet_conn), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }

                            }
                        });
                sweetAlertDialog.show();
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //String date = ""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        selectedDate = new Date(year - 1900, monthOfYear, dayOfMonth);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        nowDate = calendar.getTime();
        dateTextView.setText(df.format(selectedDate));

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        nowTime = calendar.getTime();
        selectedTime = new Date(nowTime.getYear(),nowTime.getMonth(),nowTime.getDate(),hourOfDay,minute,nowTime.getSeconds());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //String time = " " + hourOfDay + ":" + minute;
        timeTextView.setText(sdf.format(selectedTime));
    }

    public void showDate() {

        final Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                MyEventFragment.this,
                event.getDateTime().getYear()+1900,
                event.getDateTime().getMonth(),
                event.getDateTime().getDate()
        );
        dpd.setMinDate(now);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

    }

    public void showTime() {

        //Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                MyEventFragment.this,
                event.getDateTime().getHours(),
                event.getDateTime().getMinutes(),
                true
        );

        dpd.show(getActivity().getFragmentManager(), "Timepickerdialog");

    }

    public void showMapDialog() {

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_dialog);
        dialog.show();
        dialog.setCancelable(false);

        MapView mMapView;
        MapsInitializer.initialize(getActivity());

        mMapView = dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext()  , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                mMap.setMyLocationEnabled(true);

                myLocation = new LatLng(event.getLocation().getLatitude(),event.getLocation().getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                add_marker.setImageResource(R.drawable.ic_done);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation,15);
                mMap.animateCamera(cameraUpdate);
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        lastLocation = new LatLon(marker.getPosition().latitude,marker.getPosition().longitude);
                    }
                });
            }
        });

        add_marker = dialog.findViewById(R.id.add_marker);
        add_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDraggableMarker();
            }
        });


        FloatingActionButton dissmis = dialog.findViewById(R.id.dialog_dissmis);
        dissmis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marker!=null){
                    add_marker.setImageResource(R.drawable.ic_add_location);
                    marker = null;
                    mMap.clear();
                }
                else{
                    marker = null;
                    dialog.dismiss();
                }
            }
        });
    }

    public void showSuccessDialog(){

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.successful))
                .setContentText(getString(R.string.event_updated))
                .setConfirmText(getString(android.R.string.ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        simpleLocation.endUpdates();
                        sweetAlertDialog.dismiss();
                        startProfileFragment();
                    }
                });
        sweetAlertDialog.show();

    }

    public void addDraggableMarker(){

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        firstMapAction = sharedPref.getBoolean(FIRSTMAPACTION,false);

        if(!firstMapAction){
            Toast.makeText(getContext(),getString(R.string.hold_to_drag_marker),Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(FIRSTMAPACTION,true);
            editor.apply();
        }

        if(marker==null) {
            add_marker.setImageResource(R.drawable.ic_done);
            marker = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).draggable(true));
            lastLocation = new LatLon(marker.getPosition().latitude,marker.getPosition().longitude);
        }
        else{
            lastLocation = new LatLon(marker.getPosition().latitude,marker.getPosition().longitude);
            location_text.setText(lastLocation.getLatitude()+" , "+lastLocation.getLongitude());
            marker = null;
            dialog.dismiss();
        }
    }

    public void attemptUpdate(){

        boolean cancel = false;
        View focusView = null;
        title = til_title.getEditText().getText().toString();
        description = til_description.getEditText().getText().toString();

        if(TextUtils.isEmpty(title)){
            til_title.getEditText().setError(getString(R.string.please_add_title));
            focusView = til_title.getEditText();
            cancel = true;
        }
        else if (TextUtils.isEmpty(description)){
            til_description.getEditText().setError(getString(R.string.please_add_desc));
            focusView = til_description.getEditText();
            cancel = true;
        }

        else if(isDateEmpty()){
            cancel = true;
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_select_date), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        else if(isTimeEmpty()){
            cancel = true;
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_select_time), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        else if(!isDateEmpty()){
            if(!isDateValid()){

                if(isTimeEmpty()){
                    cancel = true;
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.please_select_time), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else if(!isTimeValid()){
                    cancel = true;
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.in_future), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }

        if(isLocationEmpty()){
            SimpleLocation simpleLocation = new SimpleLocation(getContext());
            lastLocation = new LatLon(simpleLocation.getLatitude(),simpleLocation.getLongitude());
        }

        if(cancel){
            if(focusView!=null){
                focusView.requestFocus();
            }
        }
        else{
            if(isOnline()){
                event.setTitle(til_title.getEditText().getText().toString());
                event.setDescription(til_description.getEditText().getText().toString());
                eventDateTime = new Date(selectedDate.getYear(),selectedDate.getMonth(),selectedDate.getDate(),selectedTime.getHours(),selectedTime.getMinutes());
                event.setDateTime(eventDateTime);
                event.setLocation(lastLocation);
                event.setCategory(spinner_category.getSelectedItemPosition());

                updateEvent(event);

            }
            else{
                Snackbar snackbar = Snackbar.make(view, getString(R.string.check_internet_conn), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    public void updateEvent(final Event event){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Events");
        reference.child(event.getEventID()).setValue(event);
        showSuccessDialog();
    }

    public void deleteEvent(Event event){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        Query query = ref.orderByChild("eventID").equalTo(event.getEventID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR DELETE EVENT", "onCancelled", databaseError.toException());
            }
        });

    }

    public void startProfileFragment(){

        Fragment fragment = new ProfileFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isDateEmpty(){
        return selectedDate ==null;
    }

    public boolean isTimeEmpty(){
        return selectedTime ==null;
    }

    public boolean isLocationEmpty(){
        return lastLocation==null;
    }

    public boolean isDateValid(){
        return selectedDate.after(nowDate);
    }

    public boolean isTimeValid(){
        return selectedTime.after(nowTime);
    }

}
