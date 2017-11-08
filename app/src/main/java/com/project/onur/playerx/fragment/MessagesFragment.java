package com.project.onur.playerx.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.model.Chat;
import com.project.onur.playerx.model.User;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {

    DatabaseReference mReferance;
    ArrayList<String> userList;
    User user;
    SQLiteUser sqLiteUser;


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

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReferance.addListenerForSingleValueEvent(new ValueEventListener() {
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



        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_messages));
    }

}
