package com.example.geniusplaza.geniusplazachatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geniusplaza.geniusplazachatapp.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class UserProfileActivity extends AppCompatActivity {

    public DatabaseReference mDatabaseReference;
    TextView userName, userEmail;
    EditText userAboutMe;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = (TextView) findViewById(R.id.user_profile_name);
        userEmail = (TextView) findViewById(R.id.user_profile_email);
        userAboutMe = (EditText) findViewById(R.id.user_about_me);
        profilePic = (ImageView) findViewById(R.id.user_profile_pic);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        Query query = users.orderByChild("email");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userAboutMe.setText(user.aboutMe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void submitProfileChanges(View v){
        User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), userAboutMe.getText().toString());
        mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
    }
}

