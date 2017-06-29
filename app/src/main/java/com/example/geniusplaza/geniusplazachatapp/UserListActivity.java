package com.example.geniusplaza.geniusplazachatapp;

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
        //getAllUsersFromFirebase();
        displayUsers();
    }

    private void displayUsers() {
        ListView listOfUsers = (ListView)findViewById(R.id.list_of_users);

        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.user_row, FirebaseDatabase.getInstance().getReference().child("users")) {
            @Override
            protected void populateView(View v, User model, int position) {
                // Get references to the views of message.xml
                TextView userEmail = (TextView)v.findViewById(R.id.email_user);
                if(!model.email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail()) )
                    userEmail.setText(model.email);
//                // Set their text
//                userEmail.setText(model.getEmail());
            }
        };

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
/*
public class UserListActivity extends AppCompatActivity {

    private static final String TAG = "UserList";
    private DatabaseReference userlistReference;
    private ValueEventListener mUserListListener;
    ArrayList<String> usernamelist = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    ListView userListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        userlistReference = FirebaseDatabase.getInstance().getReference().child("users");
        onStart();
        userListView = (ListView) findViewById(R.id.list_of_users);


    }

    @Override
    protected void onStart() {
        super.onStart();
        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usernamelist = new ArrayList<>((ArrayList) dataSnapshot.getValue());
                usernamelist.remove(usernameOfCurrentUser());
                Log.i(TAG, "onDataChange: " + usernamelist.toString());
                arrayAdapter = new ArrayAdapter(UserListActivity.this, android.R.layout.simple_list_item_1, usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
                Toast.makeText(UserListActivity.this, "Failed to load User list.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        userlistReference.addValueEventListener(userListener);

        mUserListListener = userListener;
    }

    public String usernameOfCurrentUser() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mUserListListener != null) {
            userlistReference.removeEventListener(mUserListListener);
        }

    }


}*/
