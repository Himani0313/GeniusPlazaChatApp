package com.example.geniusplaza.geniusplazachatapp.POJO;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by geniusplaza on 6/29/17.
 */
@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String name;
    public String fireBaseToken;
    public String aboutMe;
    public String profileImg;
    public User() {
    }

    public User(String uid, String email, String name, String fireBaseToken, String aboutMe, String profileImg) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.fireBaseToken = fireBaseToken;
        this.aboutMe = aboutMe;
        this.profileImg = profileImg;
    }
}
