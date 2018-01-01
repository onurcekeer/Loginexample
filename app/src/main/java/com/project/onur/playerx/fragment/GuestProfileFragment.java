package com.project.onur.playerx.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.view.View;
import android.view.ViewGroup;

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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.location.SimpleLocation;


public class GuestProfileFragment extends Fragment{

    User user,otherUser;
    Event event;
    SQLiteUser sqLiteUser;
    RecyclerView recycler_view;
    SimpleLocation simpleLocation;

    List<Event> event_list;
    Date nowTime;



    public GuestProfileFragment() {
        // Required empty public constructor
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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("EVENT");
        }


        otherUser = new User();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        Query query = reference.orderByChild("userID").equalTo(event.getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    otherUser = dataSnapshot.child(event.getUserID()).getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_profile, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        CollapsingToolbarLayout mCollapsingtoolbar = v.findViewById(R.id.toolbar_layout);
        mCollapsingtoolbar.setTitle(event.getUsername());


        CircleImageView profilImage = v.findViewById(R.id.profile_image);

        Picasso.with(v.getContext())
                .load(event.getProfileURL())
                .resize(90, 90)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(profilImage);


//        mCollapsingtoolbar.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChatFragment(otherUser);
            }
        });

        event_list = new ArrayList<>();

        recycler_view = v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);

        nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        nowTime = calendar.getTime();
        getEventData();

    }


    public void getEventData(){

        event_list.clear();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Events");
        Query query = myRef.orderByChild("userID").equalTo(event.getUserID());
        query.addValueEventListener(new ValueEventListener() {
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
        });

    }

    public void setAdapter(final List<Event> list){

        SimpleRecyclerAdapter recycler_adapter = new SimpleRecyclerAdapter(list, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Event event = list.get(position);
                startEventDetailFragment(event);
            }
        });
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(recycler_adapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

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

    private void startChatFragment(User user){

        Fragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}
