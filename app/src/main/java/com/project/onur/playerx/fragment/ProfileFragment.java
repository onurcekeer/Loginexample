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
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.onur.playerx.CustomItemClickListener;
import com.project.onur.playerx.model.Event;
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


public class ProfileFragment extends Fragment{

    FirebaseAuth mAuth;
    User user;
    SQLiteUser sqLiteUser;

    RecyclerView recycler_view;
    SimpleLocation simpleLocation;
    TextView emptyView;
    Button create_event;

    List<Event> event_list;
    Date nowTime;


    public ProfileFragment() {
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


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        mAuth = FirebaseAuth.getInstance();

        CollapsingToolbarLayout mCollapsingtoolbar = (CollapsingToolbarLayout)v.findViewById(R.id.toolbar_layout);
        mCollapsingtoolbar.setTitle(user.getUsername());

        CircleImageView profilImage = (CircleImageView)v.findViewById(R.id.profile_image);

        Picasso.with(v.getContext())
                .load(user.getProfilURL())
                .resize(100, 100)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(profilImage);


        //mCollapsingtoolbar.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryDark));
        mCollapsingtoolbar.setExpandedTitleMarginBottom(50);
        //mCollapsingtoolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettingsFragment();
            }
        });


        event_list = new ArrayList<>();

        create_event = v.findViewById(R.id.create_event_button);
        recycler_view = v.findViewById(R.id.recycler_view);
        emptyView = v.findViewById(R.id.empty_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateEventFragment();
            }
        });

        nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        nowTime = calendar.getTime();
        getEventData();

    }

    public void getEventData(){

        event_list.clear();
        recycler_view.setAdapter(null);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Events");
        Query query = myRef.orderByChild("userID").equalTo(user.getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void setAdapter(final List<Event> list){

        if (list.isEmpty()) {
            recycler_view.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            create_event.setVisibility(View.VISIBLE);
        }
        else{
            SimpleRecyclerAdapter recycler_adapter = new SimpleRecyclerAdapter(list, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Event event = list.get(position);
                    startMyEventFragment(event);
                }
            });
            recycler_view.setHasFixedSize(true);
            recycler_view.setAdapter(recycler_adapter);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            create_event.setVisibility(View.GONE);
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

    private void startSettingsFragment(){
        Fragment fragment = new SettingsFragment();
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


}
