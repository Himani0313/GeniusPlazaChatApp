package com.example.geniusplaza.geniusplazachatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserToUserChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_to_user_chat);
        Bundle bundle = getIntent().getExtras();
        String userName = bundle.getString("userName");
        setTitle(userName);
    }
}
