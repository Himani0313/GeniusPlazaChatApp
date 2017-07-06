package com.example.geniusplaza.geniusplazachatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geniusplaza.geniusplazachatapp.POJO.Chat;
import com.example.geniusplaza.geniusplazachatapp.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;

import static android.R.attr.bitmap;

public class UserProfileActivity extends AppCompatActivity {

    public DatabaseReference mDatabaseReference;
    TextView userName, userEmail;
    EditText userAboutMe;
    ImageView profilePic;
    final int REQUEST_IMAGE_CAPTURE = 100;
    public static String imageEncoded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle("User Profile");
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
                byte[] decodedString = Base64.decode(user.profileImg, Base64.DEFAULT);

                Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                profilePic.setImageBitmap(bm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changeProfilePic (View v){
        onLaunchCamera();
    }
    public void submitProfileChanges(View v){
        BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseInstanceId.getInstance().getToken(), userAboutMe.getText().toString(), imageEncoded);
        mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
        Toast.makeText(getApplicationContext(),"Changes Successful",Toast.LENGTH_SHORT);
    }
    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePic.setVisibility(View.VISIBLE);
            profilePic.setImageBitmap(imageBitmap);
            //encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }
    /*public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }*/
}

