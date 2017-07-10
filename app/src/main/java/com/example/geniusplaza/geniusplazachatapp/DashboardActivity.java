package com.example.geniusplaza.geniusplazachatapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.geniusplaza.geniusplazachatapp.CustomerServiceBot.MainActivityBot;
import com.example.geniusplaza.geniusplazachatapp.POJO.User;
import com.example.geniusplaza.geniusplazachatapp.Tutor.MainActivityTutor;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;

public class DashboardActivity extends AppCompatActivity {
    int SIGN_IN_REQUEST_CODE = 100;
    public DatabaseReference mDatabaseReference;
    public String imageEncoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            //displayChatMessages();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();

                DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");


                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            User user = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);

                            if(user.aboutMe == null && user.profileImg.equals(imageEncoded)){
                                User userAlt = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), null, imageEncoded);
                                mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAlt);
                            }
                            else if(user.aboutMe != null && user.profileImg.equals(imageEncoded)){
                                User userAlt = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), user.aboutMe, imageEncoded);
                                mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAlt);
                            }
                            else if(user.aboutMe == null && !user.profileImg.equals(imageEncoded)){
                                User userAlt = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), null, user.profileImg);
                                mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAlt);
                            }
                            else{
                                User userAlt = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), user.aboutMe, user.profileImg);
                                mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAlt);
                            }


                        }
                        else{
                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), null, imageEncoded);
                            mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }


            //displayChatMessages();

        } else {
            Toast.makeText(this,
                    "We couldn't sign you in. Please try again later.",
                    Toast.LENGTH_LONG)
                    .show();

            // Close the app
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DashboardActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        } else if (item.getItemId() == R.id.user_profile) {
            Intent i = new Intent(this, UserProfileActivity.class);
            startActivity(i);
        }
        return true;
    }

    public void buttonGoToGroupChat(View v) {
        Intent i = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void buttonGoToUserToUserChat(View v) {
        Intent i = new Intent(DashboardActivity.this, UserListActivity.class);
        startActivity(i);
    }

    public void customerServiceBotClicked(View v) {
        Intent i = new Intent(DashboardActivity.this, MainActivityBot.class);
        startActivity(i);
    }
    public void tutorButtonClicked(View v) {
        Intent i = new Intent(DashboardActivity.this, MainActivityTutor.class);
        startActivity(i);
    }
}
