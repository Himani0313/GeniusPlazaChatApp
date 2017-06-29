package com.example.geniusplaza.geniusplazachatapp;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private FirebaseListAdapter<User> adapter;
    public static final String ARG_USERS = "users";
    public List<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        displayUsers();
    }

    private void displayUsers() {
        ListView listOfUsers = (ListView)findViewById(R.id.list_of_users);

        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.user_row, FirebaseDatabase.getInstance().getReference().child("users")) {
            @Override
            protected void populateView(View v, final User model, int position) {
                // Get references to the views of message.xml
                TextView userEmail = (TextView)v.findViewById(R.id.email_user);
                TextView userName = (TextView)v.findViewById(R.id.name_user);
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
    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<User>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = dataSnapshotChild.getValue(User.class);
                    Log.d("User:", user.email.toString());
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }
//                mOnGetAllUsersListener.onGetAllUsersSuccess(users);
                Log.d("USERS:", users.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                mOnGetAllUsersListener.onGetAllUsersFailure(databaseError.getMessage());
                Log.d("ERRROR","Trial");
            }
        });
    }
}
