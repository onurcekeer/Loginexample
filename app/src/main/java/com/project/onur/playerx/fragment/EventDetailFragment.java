package com.project.onur.playerx.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.onur.playerx.Event;
import com.project.onur.playerx.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by onur on 9.10.2017 at 16:01.
 */

public class EventDetailFragment extends Fragment {

    Event event;
    GoogleMap map;


    MapView mapView;
    View view;
    public TextView text_title;
    public TextView text_description;
    public TextView text_category;
    public TextView text_username;
    public ImageView categoryImage;
    public TextView text_dateTime;
    public CircleImageView image_profile;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("EVENT");
        }

        view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(this.getActivity());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                }

                LatLng point = new LatLng(event.getLocation().getLatitude(),event.getLocation().getLongitude());
                map.addMarker(new MarkerOptions().position(point));
                map.getUiSettings().setMapToolbarEnabled(true);
                map.setMyLocationEnabled(true);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 13);
                map.moveCamera(cameraUpdate);
            }
        });

        perform(view);
        return view;
    }

    public void perform(View v) {

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(getString(R.string.event_detail_text));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        Date date = event.getDateTime();
        SimpleDateFormat sdp = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

        image_profile = v.findViewById(R.id.profile_image);
        text_username = v.findViewById(R.id.text_username);
        text_category = view.findViewById(R.id.text_category);
        categoryImage = view.findViewById(R.id.categoryImage);
        text_title = view.findViewById(R.id.text_title);
        text_description = view.findViewById(R.id.text_description);
        text_dateTime = view.findViewById(R.id.datetime);

        text_username.setText(event.getUsername());
        text_title.setText(event.getTitle());
        text_description.setText(event.getDescription());
        text_dateTime.setText(sdp.format(date));




        Picasso.with(getContext())
                .load(event.getProfileURL())
                .resize(40,40)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(image_profile);

        switch (event.getCategory()){
            case 0:
                text_category.setText(R.string.sport_text);
                categoryImage.setImageResource(R.drawable.ic_football_mini);
                break;
            case 1:
                text_category.setText(R.string.table_games_text);
                categoryImage.setImageResource(R.drawable.ic_table_games_mini);
                break;
            case 2:
                text_category.setText(R.string.concert_text);
                categoryImage.setImageResource(R.drawable.ic_concert_mini);
                break;
            case 3:
                text_category.setText(R.string.pc_games_text);
                categoryImage.setImageResource(R.drawable.ic_pc_games_mini);
                break;
            case 4:
                text_category.setText(R.string.cinema_text);
                categoryImage.setImageResource(R.drawable.ic_cinema_mini);
                break;
            case 5:
                text_category.setText(R.string.other_text);
                categoryImage.setImageResource(R.drawable.ic_other_mini);
                break;
            default:
                break;
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
