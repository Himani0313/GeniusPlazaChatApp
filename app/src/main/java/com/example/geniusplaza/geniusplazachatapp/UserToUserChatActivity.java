package com.example.geniusplaza.geniusplazachatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.geniusplaza.geniusplazachatapp.POJO.Chat;
import com.example.geniusplaza.geniusplazachatapp.POJO.User;
import com.example.geniusplaza.geniusplazachatapp.Utils.Constants;
import com.example.geniusplaza.geniusplazachatapp.fcm.MyFirebaseMessagingService;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserToUserChatActivity extends AppCompatActivity {
    LinearLayout layout;
    FloatingActionButton sendButton;
    EditText input;
    ScrollView scrollView;
    private DatabaseReference reference1, reference2;
    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    private FirebaseListAdapter<Chat> adapter;
    public TextView messageText, messageUser, messageTime;
    public String messageTextInput, currentUserImageUrl, receiverUserImageUrl;
    final int REQUEST_IMAGE_CAPTURE = 100;
    ImageView messageImage, userProfilePic;
    RelativeLayout relativeLayout;

    ListView listOfMessages;

    private String sender;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_to_user_chat);
        Bundle bundle = getIntent().getExtras();
        final String userName = bundle.getString("userName");
        receiver = getIntent().getStringExtra("userName");
        receiverUserImageUrl = getIntent().getStringExtra("profileImg");
        setTitle(userName);

        listOfMessages = (ListView) findViewById(R.id.list_of_messages);
        sendButton = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        messageImage = (ImageView) findViewById(R.id.messageImage);
        //userProfilePic = (ImageView) findViewById(R.id.messageProfilePic);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference1 = firebaseDatabase.getReference();
        reference2 = firebaseDatabase.getReference();

        //getting current logged user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sender = currentUser.getDisplayName();


        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = users.orderByChild("email");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentUserImageUrl = user.profileImg;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageTextInput = input.getText().toString();

                if (!messageTextInput.equals("")) {
                    Log.d("Sender and receiver: " + sender, receiver);

                    reference1.child("messages").child(sender + "_" + receiver).push().setValue(new Chat(sender, receiver, messageTextInput));
                    reference2.child("messages").child(receiver + "_" + sender).push().setValue(new Chat(sender, receiver, messageTextInput));

                    input.setText("");

//                    MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
//                    myFirebaseMessagingService.onMessageReceived(new RemoteMessage(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                    .setContentText(messageTextInput)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setPriority(Notification.PRIORITY_HIGH);


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0/* ID of notification */, mBuilder.build());

                }

            }
        });

        displayChatMessages();

    }

    private void displayChatMessages() {

        adapter = new FirebaseListAdapter<Chat>(this, Chat.class,
                R.layout.message_area, FirebaseDatabase.getInstance().getReference().child("messages").child(sender + "_" + receiver)) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                // Get references to the views of message.xml
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                messageImage = (ImageView) v.findViewById(R.id.messageImage);
                userProfilePic = (ImageView) v.findViewById(R.id.messageProfilePic);
                relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
                // Set their text
                if(model.getImageURL() != null){
                    messageText.setVisibility(View.GONE);
                    messageUser.setVisibility(View.VISIBLE);
                    messageTime.setVisibility(View.VISIBLE);
                    messageImage.setVisibility(View.VISIBLE);
                    if (model.getSender().equals(sender)) {
                        messageUser.setText("You");
                        messageText.setGravity(Gravity.START);
                        messageUser.setGravity(Gravity.START);
                        messageTime.setGravity(Gravity.START);
                        messageImage.setScaleType(ImageView.ScaleType.FIT_START);
                        relativeLayout.setBackgroundResource(R.drawable.rounded_color_blue);

                        byte[] decodedString = Base64.decode(currentUserImageUrl, Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        userProfilePic.setImageBitmap(bm);
                    } else {
                        messageUser.setText(model.getSender());
                        messageText.setGravity(Gravity.END);
                        messageUser.setGravity(Gravity.END);
                        messageTime.setGravity(Gravity.END);
                        messageImage.setScaleType(ImageView.ScaleType.FIT_END);
                        relativeLayout.setBackgroundResource(R.drawable.rounded_color_grey);

                        byte[] decodedString = Base64.decode(receiverUserImageUrl, Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        userProfilePic.setImageBitmap(bm);
                    }

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getTimestamp()));

                    byte[] decodedString = Base64.decode(model.getImageURL(), Base64.DEFAULT);
                    Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    messageImage.setImageBitmap(bm);
                    messageText.setVisibility(View.VISIBLE);
                }
                else {
                    messageImage.setVisibility(View.GONE);
                    messageText.setVisibility(View.VISIBLE);
                    messageUser.setVisibility(View.VISIBLE);
                    messageTime.setVisibility(View.VISIBLE);
                    messageText.setText(model.getMessage());

                    Log.d("hello", model.getSender());
                    Log.d("hello from sender", sender);
                    if (model.getSender().equals(sender)) {
                        messageUser.setText("You");
                        messageText.setGravity(Gravity.START);
                        messageUser.setGravity(Gravity.START);
                        messageTime.setGravity(Gravity.START);
                        relativeLayout.setBackgroundResource(R.drawable.rounded_color_blue);

                        byte[] decodedString = Base64.decode(currentUserImageUrl, Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        Log.d("Url String: ", currentUserImageUrl);

                        userProfilePic.setImageBitmap(bm);
                    } else {
                        messageUser.setText(model.getSender());
                        messageText.setGravity(Gravity.END);
                        messageUser.setGravity(Gravity.END);
                        messageTime.setGravity(Gravity.END);
                        relativeLayout.setBackgroundResource(R.drawable.rounded_color_grey);

                        byte[] decodedString = Base64.decode(receiverUserImageUrl, Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        userProfilePic.setImageBitmap(bm);
                    }

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getTimestamp()));
                }


            }

        };

        listOfMessages.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    public void addMessageBox(String message, int type) {

        TextView textView = new TextView(UserToUserChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.rounded_color_grey);
        } else {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.rounded_color_blue);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void addPictureFab(View v) {
        onLaunchCamera();
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
            messageImage.setVisibility(View.VISIBLE);
            messageImage.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        reference1.child("messages").child(sender + "_" + receiver).push().setValue(new Chat(sender,receiver,imageEncoded,1));
        reference2.child("messages").child(receiver + "_" + sender).push().setValue(new Chat(sender,receiver,imageEncoded,1));
    }
}