package com.nebbs.counterstrikestats.objects;

import java.io.Serializable;

public class User implements Serializable {

    private String steamID;
    private String id;

    public User(){}

    public String getSteamID() {
        return steamID;
    }

    public void setSteamID(String steamID) {
        this.steamID = steamID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
