package com.nebbs.counterstrikestats.objects;


import java.io.Serializable;

public class SteamUserAccount implements Serializable {

    private String ID;
    private String vanityID;
    private String avatar;
    private String currentState;
    private String comVisibility; // -1 NOT VISIBLE  3 VISIBLE
    private String profileState;
    private String lastLogOff;
    private String commentPermission;

    public String getID() {
        return ID;
    }

    public String getCurrentStateFormatted() {

        int s = Integer.parseInt(currentState);

        switch (s){
            case 0:
                return "Offline";
            case 1:
                return "Online";
            case 2:
                return "Busy";
            case 3:
                return "Away";
            case 4:
                return "Snooze";
            case 5:
                return "Looking To Trade";
            case 6:
                return "Looking To Play";
            default:
                return "Offline";
        }
    }

    public String getCurrentState(){
        return currentState;
    }

    public void setCurrentState(String state) {
        this.currentState = state;
    }

    public String getComVisibility(){
        return comVisibility;
    }

    public String getComVisibilityFormatted() {
        int s = Integer.parseInt(comVisibility);

        switch (s){
            case 3:
                return "Public";
            default:
                return "Private";
        }

    }

    public void setComVisibility(String comVisibility) {
        this.comVisibility = comVisibility;
    }

    public String getProfileState() {
        return profileState;
    }

    public String getProfileStateFormatted() {
        int s = Integer.parseInt(profileState);
        if(s == 1){
            return "Configured";
        }else{
            return "UN-Configured";
        }
    }

    public void setProfileState(String profileState) {
        this.profileState = profileState;
    }

    public String getLastLogOff() {
        return lastLogOff;
    }

    public void setLastLogOff(String lastLogOff) {
        this.lastLogOff = lastLogOff;
    }

    public String getCommentPermission() {
        return commentPermission;
    }

    public void setCommentPermission(String commentPermission) {
        this.commentPermission = commentPermission;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getVanityID() {
        return vanityID;
    }

    public void setVanityID(String vanityID) {
        this.vanityID = vanityID;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
