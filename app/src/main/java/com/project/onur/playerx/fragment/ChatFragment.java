package com.project.onur.playerx.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.project.onur.playerx.MainApp;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.adapter.ChatRecyclerAdapter;
import com.project.onur.playerx.fcm.FcmNotificationBuilder;
import com.project.onur.playerx.model.Chat;
import com.project.onur.playerx.model.User;
import com.project.onur.playerx.model.GetChat;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by onur on 17.9.2017 at 14:39.
 */

public class ChatFragment extends android.support.v4.app.Fragment implements TextView.OnEditorActionListener {


    FirebaseUser mUser;
    DatabaseReference mDatabase;
    User user, otherUser;
    SQLiteUser sqLiteUser;
    String message;
    ChatRecyclerAdapter mChatRecyclerAdapter;

    Toolbar toolbar;
    EditText edit_message;
    RecyclerView recyclerView;
    View view;
    FloatingActionButton fab_send;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteUser = new SQLiteUser(getActivity().getApplicationContext());

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            otherUser = (User) bundle.getSerializable("USER");
        }

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_chat, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        edit_message = v.findViewById(R.id.edit_text_message);
        recyclerView = v.findViewById(R.id.recycler_view_chat);
        fab_send = v.findViewById(R.id.chatSendButton);

        edit_message.setOnEditorActionListener(this);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(otherUser.getUsername());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edit_message.getText().toString())){
                    sendMessage();
                }

            }
        });

        getMessageFromFirebaseUser(user.getUserID(),otherUser.getUserID());

    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEND) {
            if(!TextUtils.isEmpty(edit_message.getText().toString())){
                sendMessage();
            }
            return true;
        }
        return false;
    }



    private void sendMessage() {

        String message = edit_message.getText().toString();
        String senderUid = user.getUserID();
        String receiverUid = otherUser.getUserID();
        Map<String, String> timestamp = ServerValue.TIMESTAMP;

        UUID UUid = UUID.randomUUID();
        final String uniqeUUID = UUid.toString();

        final Chat chat = new Chat(senderUid,receiverUid,message,timestamp);


        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;
        final String chat_rooms = "chat_rooms";

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child(chat_rooms).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e("CHAT", "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    databaseReference.child(chat_rooms).child(room_type_1).child(uniqeUUID).setValue(chat);

                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e("CHAT", "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    databaseReference.child(chat_rooms).child(room_type_2).child(uniqeUUID).setValue(chat);

                } else {
                    Log.e("CHAT", "sendMessageToFirebaseUser: success");
                    databaseReference.child(chat_rooms).child(room_type_1).child(uniqeUUID).setValue(chat);
                    getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid);
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(user.getUsername(),
                        chat.message,
                        chat.senderUid,
                        user.getFcmToken(),
                        otherUser.getFcmToken());

                onSendMessageSuccess();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onSendMessageFailure();
            }
        });


    }



    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;
        final String chat_rooms = "chat_rooms";

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(chat_rooms).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e("CHAT", "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(chat_rooms)
                            .child(room_type_1).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            GetChat chat = dataSnapshot.getValue(GetChat.class);
                            onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            onGetMessagesFailure();
                        }
                    });

                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e("CHAT", "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(chat_rooms)
                            .child(room_type_2).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            GetChat chat = dataSnapshot.getValue(GetChat.class);
                            onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            onGetMessagesFailure();
                        }
                    });
                } else {
                    Log.e("CHAT", "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetMessagesFailure();
            }
        });
    }


    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }


    public void onSendMessageSuccess() {
        edit_message.setText("");
    }


    public void onSendMessageFailure() {
        Toast.makeText(getActivity(), "Mesaj Gönderilemedi", Toast.LENGTH_SHORT).show();
    }


    public void onGetMessagesSuccess(GetChat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<GetChat>(),otherUser);
            recyclerView.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        recyclerView.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }


    public void onGetMessagesFailure() {
        Context context = getContext();
        if(context != null){
            Toast.makeText(getContext(), "Mesaj alınamadı", Toast.LENGTH_SHORT).show();
        }

    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApp.setChatActivityOpen(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApp.setChatActivityOpen(false);
    }


}
