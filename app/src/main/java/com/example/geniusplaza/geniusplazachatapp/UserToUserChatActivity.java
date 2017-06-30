package com.example.geniusplaza.geniusplazachatapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.geniusplaza.geniusplazachatapp.POJO.Chat;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class UserToUserChatActivity extends AppCompatActivity {
    LinearLayout layout;
    Button sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_to_user_chat);
        Bundle bundle = getIntent().getExtras();
        final String userName = bundle.getString("userName");
        setTitle(userName);
        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (Button)findViewById(R.id.button_send);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://geniusplazachatapp.firebaseio.com/message/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "_" + userName);
        reference2 = new Firebase("https://geniusplazachatapp.firebaseio.com/message/" + userName + "_" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userNameString = map.get("user").toString();

                if(userNameString.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){ //YOUR MESSAGE
                    addMessageBoxSelf("You:-\n" + message);
                }
                else{ //THEIR MESSAGE
                    addMessageBoxThem(userName + ":-\n" + message);
                }
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBoxSelf(String message){
        TextView textView = new TextView(UserToUserChatActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.RIGHT);

        textView.setBackgroundResource(R.drawable.rounded_color_blue);

        layout.addView(textView);
        messageArea.setText("");
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
    public void addMessageBoxThem(String message){
        TextView textView = new TextView(UserToUserChatActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);
        textView.setTextColor(Color.BLACK);

        textView.setBackgroundResource(R.drawable.rounded_color_grey);

        layout.addView(textView);
        messageArea.setText("");
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}