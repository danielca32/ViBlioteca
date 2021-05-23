package com.danielcastro.viblioteca;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {


    String uid;
    //Creating member variable for FirebaseAuth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //NavigationBar
    private BottomNavigationView bottomNavView;
    //User
    private DatabaseReference db;
    private User user = new User();
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
       uid = mAuth.getCurrentUser().getUid();
        //We listen to the User data only here and spread the reference all across the app.
        db.child("users/" + uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.setRole(snapshot.getValue(User.class).getRole());
                user.setUID(snapshot.getValue(User.class).getUID());
                user.setActive(snapshot.getValue(User.class).isActive());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        fragment = new BooksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        bottomNavView = findViewById(R.id.navigation);
        bottomNavView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        fragment = null;
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.settings_item:
                                fragment = new SettingsFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                break;
                            case R.id.library_books:
                                fragment = new BooksFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                break;
                            case R.id.loaned_books:
                                fragment = new LoanFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                break;
                            case R.id.logout_item:
                                try {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.putExtra("logout", "yes");
                                    startActivity(intent);
                                } catch (Error error) {
                                    System.out.println("ERROR!");//TODO Implement proper errors.
                                }
                                break;
                        }

                        return false;
                    }
                });
    } }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //We override default behaviour of back button, to set the list of books if we press back on other fragments,
    //and exit the app if we are already there.
    @Override
    public void onBackPressed(){
        if (fragment.getClass().equals(BooksFragment.class)){
           super.onBackPressed();
        } else {
            fragment = new BooksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }
}