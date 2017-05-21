package com.nebbs.counterstrikestats.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nebbs.counterstrikestats.R;
import com.nebbs.counterstrikestats.objects.User;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText email;
    private EditText password;
    private TextView needAccount;
    private TextView alreadyHaveAccount;
    private EditText passwordAgain;
    private Button loginBtn;
    private Button registerBtn;
    private ProgressBar pb;
    private FirebaseAuth auth;
    private boolean loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginState = true;

        // Views
        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        passwordAgain = (EditText) findViewById(R.id.passwordAgainInput);

        // Buttons
        loginBtn = (Button) findViewById(R.id.loginButton);
        registerBtn = (Button) findViewById(R.id.registerAccountButton);
        needAccount = (TextView) findViewById(R.id.needAccount);
        alreadyHaveAccount =(TextView) findViewById(R.id.gotAnAccount);

        // Progress Bar
        pb = (ProgressBar) findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);

        // Setup listeners
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        needAccount.setOnClickListener(this);
        alreadyHaveAccount.setOnClickListener(this);

        //Initialise authentication
        auth = FirebaseAuth.getInstance();

        // Updates the UI
        updateUI();
    }

    // Logs in user and directs to the main activity if successful.
    private void loginUser(){
        pb.setVisibility(View.VISIBLE);
        boolean valid = true;

        // Checks if the email is empty.
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required.");
            valid = false;
        }else{
            email.setError(null);
        }

        // Checks if the password has been entered.
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required.");
            valid = false;
        }

        // Attempts to login if both password and email are set.
        if(valid){
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pb.setVisibility(View.INVISIBLE);

                            // Authentication has worked.
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");

                                // Get the authentication information.
                                final FirebaseUser user = auth.getCurrentUser();

                                // Query the database looking for remaining information.
                                DatabaseReference database;
                                database = FirebaseDatabase.getInstance().getReference();

                                // Wait for the database info to be received.
                                database.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        // Look through each DataSnapshot.
                                        for(DataSnapshot data : dataSnapshot.getChildren()){

                                            // If the database has the user in, then copy the information from it into
                                            // a local object -> User.
                                            if(data.child(user.getUid()).exists()){
                                                Log.d(TAG, "Found user info in database.");

                                                // User has been found store the data into object.
                                                User a = data.child(user.getUid()).getValue(User.class);

                                                // Call the main activity.
                                                Intent i = new Intent();
                                                i.setClass(getApplicationContext(), MainActivity.class);
                                                i.putExtra("user", a);
                                                startActivity(i);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        showToast("Failed To Login", Toast.LENGTH_SHORT);
                                    }
                                });
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                showToast("Authentication failed.", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    // Registers an account.
    private void registerAccount(){
        boolean valid = true;

        // Checks if the email has been entered.
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required.");
            valid = false;
        }else{
            email.setError(null);
        }

        // Checks if the password has been entered.
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required.");
            valid = false;
        }

        // Checks if the password again is present.
        if(TextUtils.isEmpty(passwordAgain.getText().toString())){
            passwordAgain.setError("Required.");
            valid = false;
        }

        // Checks if the passwords match.
        if(!TextUtils.equals(password.getText().toString(), passwordAgain.getText().toString())){
            passwordAgain.setError("Passwords Do Not Match.");
            valid = false;
        }

        // If all above valid attempt to register a new account.
        if(valid){
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();

                        // Sends the email verification to the user.
                        user.sendEmailVerification();
                        showToast("Sent Email Verification.", Toast.LENGTH_SHORT);

                        // Sends the data to the real time database to store steam id.
                        User a = new User();
                        a.setId(user.getUid());
                        a.setSteamID("");
                        DatabaseReference database;
                        database = FirebaseDatabase.getInstance().getReference();
                        database.child("users").child(a.getId()).setValue(a);
                    }else{
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        showToast("Authentication failed.", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    // Shows a toast on screen.
    private void showToast(String text, int duration){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = auth.getCurrentUser();

        // If the user is still authenticated, login.
        if(user != null){
            User u = new User();
            Intent i = new Intent();
            i.setClass(getApplicationContext(), MainActivity.class);
            i.putExtra("user", u);
            startActivity(i);
        }
    }

    // Updates all of the UI elements and hides the ones that don't need to be seen
    private void updateUI(){
        if(loginState){
            email.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            needAccount.setVisibility(View.VISIBLE);

            passwordAgain.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);
            alreadyHaveAccount.setVisibility(View.INVISIBLE);
        }else{
            email.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
            needAccount.setVisibility(View.INVISIBLE);

            passwordAgain.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            alreadyHaveAccount.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.loginButton){
            loginUser();
        }else if(i == R.id.registerAccountButton){
            registerAccount();
        }
        else if(i == R.id.needAccount){
            if(loginState){
                loginState = false;
                updateUI();
            }
        }else if(i == R.id.gotAnAccount){
            if(loginState == false){
                loginState = true;
                updateUI();
            }
        }

    }
}
