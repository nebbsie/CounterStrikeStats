package com.nebbs.counterstrikestats.activities;

import android.app.Activity;
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
import com.nebbs.counterstrikestats.R;

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

    private void loginUser(){
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
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                System.out.println(user.getDisplayName());

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
                    }else{
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        System.out.println(currentUser);
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
