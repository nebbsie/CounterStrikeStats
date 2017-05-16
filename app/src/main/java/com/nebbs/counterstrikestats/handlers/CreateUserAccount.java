package com.nebbs.counterstrikestats.handlers;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;

public class CreateUserAccount extends AsyncTask<URL, Void, Boolean>{

    private boolean success = false;

    @Override
    protected Boolean doInBackground(URL... urls) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fb = database.getReference("users");


        return success;
    }
}
