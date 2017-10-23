package com.project.onur.playerx.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.onur.playerx.CustomItemClickListener;
import com.project.onur.playerx.Event;
import com.project.onur.playerx.LatLon;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.SimpleRecyclerAdapter;
import com.project.onur.playerx.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.location.SimpleLocation;


public class HomeFragment extends Fragment implements View.OnClickListener{

    LinearLayout scroolView_layout;
    LinearLayout selectedItem_layout;
    TextView text_category ;
    ImageView clear_category;
    MaterialSearchView searchView;
    RecyclerView recycler_view;
    SimpleLocation simpleLocation;
    ValueEventListener valueEventListener, valueEventListenerCategory;
    DatabaseReference myRef;

    List<Event> event_list;
    Date nowTime;
    User user;
    SQLiteUser sqLiteUser;
    private final int REQUEST_CODE = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
        Log.e("user",user.toString());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        simpleLocation = new SimpleLocation(getContext());

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

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

        searchView =  v.findViewById(R.id.search_view);
        searchView.setVoiceSearch(true); //or false
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                scroolView_layout.setVisibility(View.GONE);
                selectedItem_layout.setVisibility(View.VISIBLE);
                text_category.setText(query);
                //searchOnFirebase(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });


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
                    double distance = SimpleLocation.calculateDistance(simpleLocation.getLatitude(), simpleLocation.getLongitude(),eventLocation.getLatitude(),eventLocation.getLongitude());
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
                    double distance = SimpleLocation.calculateDistance(simpleLocation.getLatitude(), simpleLocation.getLongitude(),eventLocation.getLatitude(),eventLocation.getLongitude());
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

        checkLocationPermission();
    }

    public void getEventData(){

        event_list.clear();

        myRef.addValueEventListener(valueEventListener);

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == 0){
            restartFragment();
            String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider!=null){
                Log.v("GPS", " Location providers: "+provider);
                getEventData();

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
                setCategoryFilter(0);
                break;

            case R.id.lineer_table_game:
                text_category.setText(R.string.table_games_text);
                setCategoryFilter(1);
                break;

            case R.id.lineer_concert:
                text_category.setText(R.string.concert_text);
                setCategoryFilter(2);
                break;

            case R.id.lineer_pc_game:
                text_category.setText(R.string.pc_games_text);
                setCategoryFilter(3);
                break;

            case R.id.lineer_cinema:
                text_category.setText(R.string.cinema_text);
                setCategoryFilter(4);
                break;

            case R.id.lineer_other:
                text_category.setText(R.string.other_text);
                setCategoryFilter(5);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
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



    }

    public void setAdapter(final List<Event> list){

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
        myRef.removeEventListener(valueEventListener);
        myRef.removeEventListener(valueEventListenerCategory);

    }

    public void searchOnFirebase(String text){

        event_list.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        Query query = databaseReference.getParent()
                .startAt("[a-zA-Z0-9]*")
                .endAt(text);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Date eventDate = postSnapshot.child("dateTime").getValue(Date.class);

                    if(eventDate.after(nowTime))
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
        });
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

    public void checkLocationPermission(){

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
