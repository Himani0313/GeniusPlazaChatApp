package com.example.geniusplaza.geniusplazachatapp.POJO;

/**
 * Created by geniusplaza on 7/3/17.
 */

public class UserFriend {
    public String uid;
    public String email;
    public String name;
    public String fireBaseToken;
    public int friendstatus;
    public UserFriend() {
    }

    public UserFriend(String uid, String email, String name, String fireBaseToken, int friendstatus) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.fireBaseToken = fireBaseToken;
        this.friendstatus = friendstatus;
    }
}
