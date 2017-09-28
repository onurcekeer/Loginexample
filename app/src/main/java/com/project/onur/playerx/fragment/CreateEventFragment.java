package com.project.onur.playerx.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.onur.playerx.CharacterCountErrorWatcher;
import com.project.onur.playerx.ItemData;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SpinnerAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import im.delight.android.location.SimpleLocation;

/**
 * Created by onur on 22.9.2017 at 00:44.
 */

public class CreateEventFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{


    Date eventDate, now, eventTime;
    GoogleMap mMap;
    LatLng myLocation,lastLocation;
    Marker marker;

    TextView dateTextView, timeTextView, location_text;
    FloatingActionButton add_marker;
    Dialog dialog;


    public CreateEventFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        dateTextView = v.findViewById(R.id.date_text);
        dateTextView.setClickable(true);
        timeTextView = v.findViewById(R.id.time_text);
        timeTextView.setClickable(true);
        location_text = v.findViewById(R.id.location_text);


        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(getString(R.string.create_event));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        Spinner sp = (Spinner) v.findViewById(R.id.spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(),
                R.layout.spinner_row, R.id.txt, list);
        sp.setAdapter(adapter);

        TextInputLayout title = v.findViewById(R.id.text_title);
        TextInputLayout description = v.findViewById(R.id.text_description);

        title.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(title, 1, 80));
        description.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(description, 1, 200));


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

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //String date = ""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        eventDate = new Date(year - 1900, monthOfYear, dayOfMonth);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        now = new Date();
        Calendar calendar = Calendar.getInstance();
        now = calendar.getTime();
        dateTextView.setText(df.format(eventDate));
        if (eventDate.after(now)) {
            Log.e("DATE", "TARİH GEÇERLİ");
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        eventTime = new Date(0,0,0,hourOfDay,minute,0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //String time = " " + hourOfDay + ":" + minute;
        timeTextView.setText(sdf.format(eventTime));
    }

    public void showDate() {

        final Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                CreateEventFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

    }

    public void showTime() {

        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                CreateEventFragment.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
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

                SimpleLocation simpleLocation = new SimpleLocation(getContext());
                myLocation = new LatLng(simpleLocation.getLatitude(),simpleLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
            }
        });

        add_marker = dialog.findViewById(R.id.add_marker);
        add_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDraggableMarker();
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        lastLocation = marker.getPosition();
                    }
                });
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


    public void addDraggableMarker(){
        if(marker==null) {
            add_marker.setImageResource(R.drawable.ic_done);
            marker = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).draggable(true));
            lastLocation = marker.getPosition();
        }
        else{
            lastLocation = marker.getPosition();
            location_text.setText(lastLocation.latitude+" , "+lastLocation.longitude);
            marker = null;
            dialog.dismiss();
        }
    }


}