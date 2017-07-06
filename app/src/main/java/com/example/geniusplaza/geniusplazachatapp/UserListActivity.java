package com.example.geniusplaza.geniusplazachatapp;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geniusplaza.geniusplazachatapp.POJO.ChatMessage;
import com.example.geniusplaza.geniusplazachatapp.POJO.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.http.Query;

public class UserListActivity extends AppCompatActivity {
    public FirebaseListAdapter<User> adapter,adapterSearch;
    public static final String ARG_USERS = "users";
    public List<User> users;
    EditText textSearchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle("Friend List");
        displayUsers();
        textSearchTerm = (EditText)findViewById(R.id.inputSearchterm);
        final String searchvalue = textSearchTerm.getText().toString();
        textSearchTerm.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView listOfUsersSearch = (ListView)findViewById(R.id.list_of_users);

                adapterSearch = new FirebaseListAdapter<User>(UserListActivity.this, User.class,
                        R.layout.user_row, FirebaseDatabase.getInstance().getReference().child("friends: "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    @Override
                    protected void populateView(View v, final User model, int position) {
                        // Get references to the views of message.xml
                        TextView userEmail = (TextView)v.findViewById(R.id.email_user);
                        TextView userName = (TextView)v.findViewById(R.id.name_user);
//                FirebaseMessaging.getInstance().send(RemoteMessage );
                        Log.d("search",model.email);
                        Log.d("search term", searchvalue);
                        if((!model.email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) && model.email.contains(textSearchTerm.getText().toString().toLowerCase()) ){
                            userEmail.setText(model.email);
                            userName.setText(model.name);
                        }
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(UserListActivity.this,UserToUserChatActivity.class);
                                i.putExtra("userName",model.name);
                                i.putExtra("email",model.email);
                                i.putExtra("uid",model.uid);
                                i.putExtra("token",model.fireBaseToken);
                                i.putExtra("profileImg", model.profileImg);
                                startActivity(i);
                            }
                        });
//                // Set their text
//                userEmail.setText(model.getEmail());
                    }
                };
                listOfUsersSearch.setAdapter(adapterSearch);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void displayUsers() {
        ListView listOfUsers = (ListView)findViewById(R.id.list_of_users);

        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.user_row, FirebaseDatabase.getInstance().getReference().child("friends: "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            @Override
            protected void populateView(View v, final User model, int position) {
                // Get references to the views of message.xml
                TextView userEmail = (TextView)v.findViewById(R.id.email_user);
                TextView userName = (TextView)v.findViewById(R.id.name_user);
//                FirebaseMessaging.getInstance().send(RemoteMessage );
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                if(!model.email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail()) ){
                    userEmail.setText(model.email);
                    userName.setText(model.name);
                }
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(UserListActivity.this,UserToUserChatActivity.class);
                        i.putExtra("userName",model.name);
                        i.putExtra("email",model.email);
                        i.putExtra("uid",model.uid);
                        i.putExtra("token",model.fireBaseToken);
                        i.putExtra("profileImg", model.profileImg);
                        startActivity(i);
                    }
                });
//                // Set their text
//                userEmail.setText(model.getEmail());
            }
        };
        Log.d("token id:", FirebaseInstanceId.getInstance().getToken());
        listOfUsers.setAdapter(adapter);

    }

    public void addNewFriendButton(View v){
        Intent i = new Intent(this, AddFriendActivity.class);
        startActivity(i);
    }
}
