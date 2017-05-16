package com.nebbs.counterstrikestats.user;

import android.net.Uri;

import java.io.Serializable;
import java.net.URI;


public class User implements Serializable {
    private String email;
    private Uri picture;
    private String displayName;

    public User(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPicture() {
        return picture;
    }

    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
