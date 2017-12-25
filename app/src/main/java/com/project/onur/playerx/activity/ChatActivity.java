package com.project.onur.playerx.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.onur.playerx.R;
import com.project.onur.playerx.fragment.ChatFragment;
import com.project.onur.playerx.model.User;

public class ChatActivity extends AppCompatActivity {

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");
        if(user!=null){
            startChatFragment(user);
        }

    }

    private void startChatFragment(User user){

        Fragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_layout_content_chat, fragment);
        fragmentTransaction.commit();
        finishActivity(0);
    }
}
