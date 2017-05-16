package com.nebbs.counterstrikestats.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nebbs.counterstrikestats.R;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth auth;
    private EditText username;
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
        username = (EditText) findViewById(R.id.usernameInout);
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

    private void createAccount(){
        username.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);

    }

    private void updateUI(){
        if(loginState){
            username.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            needAccount.setVisibility(View.VISIBLE);

            passwordAgain.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);
            alreadyHaveAccount.setVisibility(View.INVISIBLE);
        }else{
            username.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
            needAccount.setVisibility(View.INVISIBLE);

            passwordAgain.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            alreadyHaveAccount.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        System.out.println(currentUser);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.loginButton){
            System.out.println("LOGIN");
        }else if(i == R.id.needAccount){
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
