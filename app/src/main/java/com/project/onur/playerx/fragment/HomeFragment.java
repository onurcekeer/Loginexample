package com.project.onur.playerx.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.onur.playerx.CustomItemClickListener;
import com.project.onur.playerx.model.Event;
import com.project.onur.playerx.model.LatLon;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.adapter.SimpleRecyclerAdapter;
import com.project.onur.playerx.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.location.SimpleLocation;


public class HomeFragment extends Fragment implements View.OnClickListener {

    LinearLayout scroolView_layout;
    LinearLayout selectedItem_layout;
    TextView text_category;
    ImageView clear_category;
    RecyclerView recycler_view;
    TextView emptyView;
    SwipeRefreshLayout swipeRefreshLayout;
    SimpleLocation simpleLocation;
    ValueEventListener valueEventListener, valueEventListenerCategory;
    DatabaseReference myRef;
    Location location;
    double latitude, longitude;
    LocationManager locationManager;
    List<Event> event_list;
    Date nowTime;
    User user;
    SQLiteUser sqLiteUser;
    private final int REQUEST_CODE = 1;
    public int category;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
        Log.e("user", user.toString());
        category = 11;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String LocationPermission = (Manifest.permission.ACCESS_FINE_LOCATION);

        simpleLocation = new SimpleLocation(getContext());
        if (simpleLocation.getLatitude() == 0.0) {
            if (getContext().checkCallingOrSelfPermission(LocationPermission) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location!=null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                }

            }
        }
        else {
            latitude = simpleLocation.getLatitude();
            longitude = simpleLocation.getLongitude();

        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        emptyView = v.findViewById(R.id.empty_view);
        swipeRefreshLayout = v.findViewById(R.id.srl_main);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        scroolView_layout = v.findViewById(R.id.scrollView_layout);
        selectedItem_layout = v.findViewById(R.id.selecteditem);
        text_category = v.findViewById(R.id.categoryName);
        clear_category = v.findViewById(R.id.clear);

        clear_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem_layout.setVisibility(View.GONE);
                scroolView_layout.setVisibility(View.VISIBLE);
                getEventData();
            }
        });


        myRef = FirebaseDatabase.getInstance().getReference("Events");

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateEventFragment();
            }
        });



        Toolbar toolbar =  v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_home));


        View sport = v.findViewById(R.id.lineer_sport);
        sport.setOnClickListener(this);
        View table_games = v.findViewById(R.id.lineer_table_game);
        table_games.setOnClickListener(this);
        View concert = v.findViewById(R.id.lineer_concert);
        concert.setOnClickListener(this);
        View pc_games = v.findViewById(R.id.lineer_pc_game);
        pc_games.setOnClickListener(this);
        View cinema = v.findViewById(R.id.lineer_cinema);
        cinema.setOnClickListener(this);
        View other = v.findViewById(R.id.lineer_other);
        other.setOnClickListener(this);

        event_list = new ArrayList<>();

        recycler_view = v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);

        nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        nowTime = calendar.getTime();




        valueEventListenerCategory = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Date eventDate = postSnapshot.child("dateTime").getValue(Date.class);
                    LatLon eventLocation = postSnapshot.child("location").getValue(LatLon.class);
                    double distance = SimpleLocation.calculateDistance(latitude, longitude,eventLocation.getLatitude(),eventLocation.getLongitude());
                    int int_distance = (int) distance/1000;

                    if(eventDate.after(nowTime) && int_distance < user.getRange())
                    {
                        event_list.add(postSnapshot.getValue(Event.class));
                        Log.e("EVENT",postSnapshot.toString());
                    }
                }
                setAdapter(event_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Date eventDate = postSnapshot.child("dateTime").getValue(Date.class);
                    LatLon eventLocation = postSnapshot.child("location").getValue(LatLon.class);
                    double distance = SimpleLocation.calculateDistance(latitude, longitude,eventLocation.getLatitude(),eventLocation.getLongitude());
                    int int_distance = (int) distance/1000;

                    if(eventDate.after(nowTime) && int_distance < user.getRange())
                    {
                        event_list.add(postSnapshot.getValue(Event.class));
                        Log.e("EVENT",postSnapshot.toString());
                    }
                }
                setAdapter(event_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setEnabled(false);
                if(category!=11){
                    setCategoryFilter(category);
                }
                else {
                    getEventData();
                }
            }
        });



        checkLocationEnabled();
    }

    public void getEventData(){

        category = 11;
        swipeRefreshLayout.setRefreshing(true);
        event_list.clear();
        myRef.addListenerForSingleValueEvent(valueEventListener);

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == 0){
            restartFragment();
            String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider!=null){
                Log.v("GPS", " Location providers: "+provider);
                //getEventData();

            }else{
                Toast.makeText(getContext(),"Konum aktif değil",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View v) {

        scroolView_layout.setVisibility(View.GONE);
        selectedItem_layout.setVisibility(View.VISIBLE);

        switch (v.getId()) {

            case R.id.lineer_sport:
                text_category.setText(R.string.sport_text);
                category = 0;
                setCategoryFilter(0);
                break;

            case R.id.lineer_table_game:
                text_category.setText(R.string.table_games_text);
                setCategoryFilter(1);
                category = 1;
                break;

            case R.id.lineer_concert:
                text_category.setText(R.string.concert_text);
                setCategoryFilter(2);
                category = 2;
                break;

            case R.id.lineer_pc_game:
                text_category.setText(R.string.pc_games_text);
                setCategoryFilter(3);
                category = 3;
                break;

            case R.id.lineer_cinema:
                text_category.setText(R.string.cinema_text);
                setCategoryFilter(4);
                category = 4;
                break;

            case R.id.lineer_other:
                text_category.setText(R.string.other_text);
                setCategoryFilter(5);
                category = 5;
                break;
            default:
                break;
        }
    }

    private void startCreateEventFragment(){
        Fragment fragment = new CreateEventFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setCategoryFilter(int category){

        event_list.clear();

        Query query = myRef.orderByChild("category").equalTo(category);
        query.addValueEventListener(valueEventListenerCategory);
        swipeRefreshLayout.setRefreshing(true);
    }

    public void setAdapter(final List<Event> list){

        if (list.isEmpty()) {
            recycler_view.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {

            SimpleRecyclerAdapter recycler_adapter = new SimpleRecyclerAdapter(list, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Event event = list.get(position);

                    if(event.getUserID().equals(user.getUserID())){
                        startMyEventFragment(event);
                    }
                    else{
                        startEventDetailFragment(event);
                    }

                }
            });
            recycler_view.setHasFixedSize(true);
            recycler_view.setAdapter(recycler_adapter);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }


        myRef.removeEventListener(valueEventListener);
        myRef.removeEventListener(valueEventListenerCategory);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);

    }

    public void startEventDetailFragment(Event event){

        Fragment fragment = new EventDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("EVENT", event);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    public void startMyEventFragment(Event event){

        Fragment fragment = new MyEventFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("EVENT", event);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    public void checkLocationEnabled(){

        if (!simpleLocation.hasLocationEnabled()) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(getString(R.string.allow_location_title))
                    .setContentText(getString(R.string.allow_location))
                    .setConfirmText(getString(android.R.string.ok))
                    .setCancelText(getString(android.R.string.cancel))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_CODE);
                            sweetAlertDialog.dismiss();
                        }
                    }).show();
        }
        else {
            getEventData();
        }
    }

    public void restartFragment(){
        getFragmentManager().beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }

}
