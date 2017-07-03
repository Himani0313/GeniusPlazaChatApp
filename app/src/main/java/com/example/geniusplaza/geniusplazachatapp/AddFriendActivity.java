package com.example.geniusplaza.geniusplazachatapp;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geniusplaza.geniusplazachatapp.POJO.ChatMessage;
import com.example.geniusplaza.geniusplazachatapp.POJO.User;
import com.example.geniusplaza.geniusplazachatapp.POJO.UserFriend;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {
    public FirebaseListAdapter<User> adapter,adapterSearch;
    public static final String ARG_USERS = "users";
    public List<User> users;
    EditText textSearchTerm;
    public FloatingActionButton addFriendButton, deleteFriendButton;
    public DatabaseReference mDatabaseReference;
    public static boolean userThereInFriendlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        setTitle("Add Friend");
        displayUsers();
        textSearchTerm = (EditText)findViewById(R.id.inputSearchterm);
        final String searchvalue = textSearchTerm.getText().toString();
        /*textSearchTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView listOfUsersSearch = (ListView)findViewById(R.id.list_of_users);

                adapterSearch = new FirebaseListAdapter<User>(AddFriendActivity.this, User.class,
                        R.layout.add_friend_row, FirebaseDatabase.getInstance().getReference().child("users")) {
                    @Override
                    protected void populateView(View v, final User model, int position) {
                        // Get references to the views of message.xml
                        TextView userEmail = (TextView)v.findViewById(R.id.email_user);
                        TextView userName = (TextView)v.findViewById(R.id.name_user);
                        addFriendButton = (Button) findViewById(R.id.addFriendButton);
//                FirebaseMessaging.getInstance().send(RemoteMessage );
                        Log.d("search",model.email);
                        Log.d("search term", searchvalue);
                        if((!model.email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) && model.email.contains(textSearchTerm.getText().toString().toLowerCase()) ){
                            userEmail.setText(model.email);
                            userName.setText(model.name);
                            addFriendButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("User name: ", model.name);
                                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                    User user = new User(model.uid, model.email, model.name, model.fireBaseToken);
                                    mDatabaseReference.child("friends").child(model.uid).setValue(user);
                                }
                            });
                        }
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(AddFriendActivity.this,UserToUserChatActivity.class);
                                i.putExtra("userName",model.name);
                                i.putExtra("email",model.email);
                                i.putExtra("uid",model.uid);
                                i.putExtra("token",model.fireBaseToken);
                                startActivity(i);
                            }
                        });

                    }
                };
                listOfUsersSearch.setAdapter(adapterSearch);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    private void displayUsers() {
        ListView listOfUsers = (ListView)findViewById(R.id.list_of_users);

        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.add_friend_row, FirebaseDatabase.getInstance().getReference().child("users")) {
            @Override
            protected void populateView(View v, final User model, int position) {
                // Get references to the views of message.xml
                TextView userEmail = (TextView)v.findViewById(R.id.email_user);
                TextView userName = (TextView)v.findViewById(R.id.name_user);
                addFriendButton = (FloatingActionButton) v.findViewById(R.id.addFriendButton);
                deleteFriendButton = (FloatingActionButton) v.findViewById(R.id.deleteFriendButton);
//                FirebaseMessaging.getInstance().send(RemoteMessage );
                if(!model.email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail()) ){
                    userEmail.setText(model.email);
                    userName.setText(model.name);

                    addFriendButton.setVisibility(View.VISIBLE);

                    addFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                            mDatabaseReference.child("friends: "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("the value of snapshot", dataSnapshot.child(model.uid).toString());
                                    if(dataSnapshot.child(model.uid).exists()){
                                        addFriendButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete));
                                        Toast.makeText(getApplicationContext(), "Already your friend", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Log.d("User name: ", model.name);
                                        addFriendButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add));
                                        User user = new User(model.uid, model.email, model.name, model.fireBaseToken);
                                        mDatabaseReference.child("friends: "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(model.uid).setValue(user);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    });
                    deleteFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                            mDatabaseReference.child("friends: "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("the value of snapshot", dataSnapshot.child(model.uid).toString());
                                    if(dataSnapshot.child(model.uid).exists()){
                                        Log.d("Delete test: ", model.name);
                                        dataSnapshot.child(model.uid).getRef().setValue(null);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Is not your friend", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        };
        Log.d("token id:", FirebaseInstanceId.getInstance().getToken());
        listOfUsers.setAdapter(adapter);
    }
}
