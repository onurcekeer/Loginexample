package com.project.onur.playerx.fragment;

import android.database.Cursor;
import android.os.Bundle;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.onur.playerx.CustomItemClickListener;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.adapter.SimpleRecyclerAdapter;
import com.project.onur.playerx.adapter.UsersAdapter;
import com.project.onur.playerx.model.Chat;
import com.project.onur.playerx.model.Event;
import com.project.onur.playerx.model.User;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {

    DatabaseReference mReferance;
    ArrayList<String> userList;
    ArrayList<User> userObjectList;
    User user;
    SQLiteUser sqLiteUser;
    RecyclerView recycler_view;


    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);

        mReferance = FirebaseDatabase.getInstance().getReference("chat_rooms");
        userList = new ArrayList<>();
        userObjectList = new ArrayList<>();




    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_messages));

        recycler_view = v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);
        getUsersData();


    }


    public void getUsersData(){

        userObjectList.clear();

        mReferance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    Log.e("rooms",name);

                    for(DataSnapshot dsChild : ds.getChildren()){
                        Chat chat = dsChild.getValue(Chat.class);
                        if(chat.getSenderUid().equals(user.getUserID())  || chat.getReceiverUid().equals(user.getUserID()) ){
                            if(chat.getSenderUid().equals(user.getUserID())){
                                if(!userList.contains(chat.receiverUid))
                                    userList.add(chat.receiverUid);
                            }else {
                                if(!userList.contains(chat.senderUid))
                                    userList.add(chat.senderUid);
                            }
                        }
                    }
                }

                Log.e("users", String.valueOf(userList));
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference userReferance = FirebaseDatabase.getInstance().getReference("User");
        userReferance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User mUser = ds.getValue(User.class);
                    if(userList.contains(mUser.getUserID())){
                        userObjectList.add(mUser);
                        Log.e("user Objects", String.valueOf(mUser));
                    }
                }

                setAdapter(userObjectList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void setAdapter(final ArrayList<User> users){

        UsersAdapter recycler_adapter = new UsersAdapter(users, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                User user = users.get(position);
                startChatFragment(user);
            }
        });

        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(recycler_adapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

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
