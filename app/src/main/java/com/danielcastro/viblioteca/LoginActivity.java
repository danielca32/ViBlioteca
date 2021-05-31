package com.danielcastro.viblioteca;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    //Request code, any integer can be used.
    private static final int RC_SIGN_IN = 1000;

    //Adding Google sign-in Client
    GoogleSignInClient mGoogleSignInClient;

    //Creating member variable for FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        //Building Google sign-in and sign-up option.
        //Configuring Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, use getString(R.string.default_web_client_id), this is in the values.xml file that
                // is generated from your google-services.json file (data from your firebase project), uses the google-sign-in method
                // web api key
                .requestIdToken(getString(R.string.default_web_client_id))//Default_web_client_id will be matched with the
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Sets the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //If it comes from the logout button, logout from Google too.
        if (getIntent().hasExtra("logout")) {
            mGoogleSignInClient.signOut();
        }
    }

    //Creating onStart() method.
    @Override
    public void onStart() {
        super.onStart();

        // Checking if the user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    //Calling onActivityResult to use the information about the sign-in user contains in the object.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                /*
                if (!account.getEmail().endsWith("vidalibarraquer.net")) {
                    Toast.makeText(this, "Account non-authorized.",  Toast.LENGTH_LONG).show();
                    mGoogleSignInClient.signOut();
                    throw new ApiException(new Status(requestCode, "Error, account non-authorized."));
                }

                 */
                Toast.makeText(this, R.string.google_sign_in_success, Toast.LENGTH_LONG).show();
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    throw new NullPointerException();
                }
            } catch (ApiException e) {

                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            }
        }
    }

    //Creating helper method FirebaseAuthWithGoogle().
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        //Calling get credential from the GoogleAuthProvider
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //Override th onComplete() to see we are successful or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Update UI with the sign-in user's information
                        Toast.makeText(LoginActivity.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign-in fails to display a message to the user.
                        Toast.makeText(LoginActivity.this, getString(R.string.error) + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void signInToGoogle() {
        //Calling Intent and call startActivityForResult() method
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onClick(View v) {
        signInToGoogle();
    }


    @Override
    public void onBackPressed() {

        }}


/*
    private void revokeAccess() {
        //Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately

                    }
                });
    }
 */

/*
 private void signOut() {
          // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Signed out of google",Toast.LENGTH_SHORT).show();
                    }
                });
    }
 */