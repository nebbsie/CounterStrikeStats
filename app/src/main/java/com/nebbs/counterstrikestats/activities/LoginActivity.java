package com.nebbs.counterstrikestats.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.nebbs.counterstrikestats.user.User;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private TextView needAccount;
    private TextView alreadyHaveAccount;
    private EditText passwordAgain;
    private Button loginBtn;
    private Button registerBtn;
    private ProgressBar pb;
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
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required.");
            valid = false;
        }else{
            email.setError(null);
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required.");
            valid = false;
        }

        if(valid){
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pb.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();

                                final User u = new User();
                                u.setId(user.getUid());

                                DatabaseReference database;
                                database = FirebaseDatabase.getInstance().getReference();

                                database.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                            if(data.child(u.getId()).exists()){
                                                User a = data.child(u.getId()).getValue(User.class);


                                                Log.d(TAG, "Found user info in database.");
                                                Context context = getApplicationContext();
                                                Intent i = new Intent();
                                                i.setClass(context, MainActivity.class);
                                                i.putExtra("user", u);
                                                startActivity(i);
                                            }else{

                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });



                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void registerAccount(){
        boolean valid = true;
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required.");
            valid = false;
        }else{
            email.setError(null);
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required.");
            valid = false;
        }

        if(TextUtils.isEmpty(passwordAgain.getText().toString())){
            passwordAgain.setError("Required.");
            valid = false;
        }

        if(!TextUtils.equals(password.getText().toString(), passwordAgain.getText().toString())){
            passwordAgain.setError("Passwords Do Not Match.");
            valid = false;
        }

        if(valid){
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        user.sendEmailVerification();
                        showToast("Sent Email Verification.", Toast.LENGTH_SHORT);

                        User a = new User();
                        a.setId(user.getUid());
                        a.setSteamID("");

                        DatabaseReference database;
                        database = FirebaseDatabase.getInstance().getReference();
                        database.child("users").child(a.getId()).setValue(a);

                    }else{
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
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

        if(user != null){
            System.out.println("AUTO LOGIN");
            User u = new User();

            Context context = getApplicationContext();
            Intent i = new Intent();
            i.setClass(context, MainActivity.class);
            i.putExtra("user", u);
            startActivity(i);
        }
    }

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
