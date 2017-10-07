package com.project.onur.playerx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.project.onur.playerx.R;
import com.project.onur.playerx.SimpleRecyclerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements View.OnClickListener {



    LinearLayout scroolView_layout;
    LinearLayout selectedItem_layout;
    TextView text_category ;
    ImageView clear_category;
    MaterialSearchView searchView;
    RecyclerView recycler_view;

    List<Event> event_list;
    Date nowTime;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
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
            }
        });


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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
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

        event_list = new ArrayList<Event>();

        recycler_view = v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);

        nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        nowTime = calendar.getTime();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Events");
        myRef.addValueEventListener(new ValueEventListener() {
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
                SimpleRecyclerAdapter recycler_adapter = new SimpleRecyclerAdapter(event_list, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.d("position", "Tıklanan Pozisyon:" + position);
                        Event event = event_list.get(position);
                        Toast.makeText(getContext(),"pozisyon:"+" "+position+" "+"Ad:"+event.getTitle(),Toast.LENGTH_SHORT).show();
                    }
                });
                recycler_view.setHasFixedSize(true);
                recycler_view.setAdapter(recycler_adapter);
                recycler_view.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);

                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {

        scroolView_layout.setVisibility(View.GONE);
        selectedItem_layout.setVisibility(View.VISIBLE);

        switch (v.getId()) {

            case R.id.lineer_sport:
                text_category.setText(R.string.sport_text);
                break;

            case R.id.lineer_table_game:
                text_category.setText(R.string.table_games_text);
                break;

            case R.id.lineer_concert:
                text_category.setText(R.string.concert_text);
                break;

            case R.id.lineer_pc_game:
                text_category.setText(R.string.pc_games_text);
                break;

            case R.id.lineer_cinema:
                text_category.setText(R.string.cinema_text);
                break;

            case R.id.lineer_other:
                text_category.setText(R.string.other_text);
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
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_out_right, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
