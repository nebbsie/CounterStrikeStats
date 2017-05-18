package com.nebbs.counterstrikestats.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nebbs.counterstrikestats.R;
import com.nebbs.counterstrikestats.objects.User;

public class ProfileActivity extends Activity implements View.OnClickListener {

    private String TAG = "ProfileActivity";

    private EditText steamID;
    private EditText email;
    private EditText displayName;
    private ImageView displayPicture;
    private Button updateProfile;
    private Button resendVerification;
    private TextView notVerified;
    private ProgressBar pb;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        steamID = (EditText) findViewById(R.id.steamID);
        email = (EditText) findViewById(R.id.emailProfile);
        displayName = (EditText) findViewById(R.id.displayNameProfile);
        displayPicture = (ImageView) findViewById(R.id.displayPicture);
        updateProfile = (Button) findViewById(R.id.updateInfo);
        notVerified = (TextView) findViewById(R.id.notVerified);
        resendVerification = (Button) findViewById(R.id.sendVerification);
        pb = (ProgressBar) findViewById(R.id.profileProgressBar);
        pb.setVisibility(View.INVISIBLE);

        //Initialise authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        updateProfile.setOnClickListener(this);
        resendVerification.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();

    }


    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }


    // Updates all of the UI.
    private void updateUI(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            email.setText(user.getEmail());
            displayName.setText(user.getDisplayName());

            if(user.isEmailVerified()){
                notVerified.setVisibility(View.INVISIBLE);
                resendVerification.setVisibility(View.INVISIBLE);
            }else{
                notVerified.setVisibility(View.VISIBLE);
                resendVerification.setVisibility(View.VISIBLE);
            }

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        if(data.child(user.getUid()).exists()){
                            System.out.println(data);
                            User a = data.child(user.getUid()).getValue(User.class);
                            if(!a.getSteamID().isEmpty()){
                                steamID.setText(a.getSteamID());
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    // Sets the steamID
    private void setSteamId(){
        if(!steamID.getText().toString().isEmpty()){
            user = FirebaseAuth.getInstance().getCurrentUser();

            User u = new User();
            u.setId(user.getUid());
            u.setSteamID(steamID.getText().toString());

            database.child("users").child(u.getId()).setValue(u);
            showToast("User profile updated.", Toast.LENGTH_SHORT);
        }
    }

    // Resend's the email verification to the user email address.
    private void resendEmailVerification(){
        pb.setVisibility(View.VISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sent Email Verification.");
                            showToast("Sent Email Verification.", Toast.LENGTH_SHORT);
                            pb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    // Updates the profile based on the information provided.
    private void updateProfile(){

        user = FirebaseAuth.getInstance().getCurrentUser();


        setSteamId();

        if(!user.getEmail().equals(email.getText().toString())){
            pb.setVisibility(View.VISIBLE);
            user.updateEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                                showToast("User email address updated.", Toast.LENGTH_SHORT);
                                pb.setVisibility(View.INVISIBLE);
                            }else{
                                Log.d(TAG, task.getException().getMessage());
                                showToast(task.getException().getMessage(), Toast.LENGTH_SHORT);
                                pb.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }

        if(user.getDisplayName() == null){
            pb.setVisibility(View.VISIBLE);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.getText().toString())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                showToast("User profile updated.", Toast.LENGTH_SHORT);
                                pb.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }else{
            if(!user.getDisplayName().equals(displayName.getText().toString())){
                pb.setVisibility(View.VISIBLE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.getText().toString())
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                    showToast("User profile updated.", Toast.LENGTH_SHORT);
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        }


    }

    // Shows a toast on screen.
    private void showToast(String text, int duration){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == updateProfile.getId()){
            updateProfile();
        }else if(i == resendVerification.getId()){
            resendEmailVerification();
        }
    }
}
