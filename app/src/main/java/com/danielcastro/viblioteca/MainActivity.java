package com.danielcastro.viblioteca;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {


    private static Fragment fragment;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private BottomNavigationView bottomNavView;

    public static void setFragment(Fragment updateFragment) {
        fragment = updateFragment;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            String uid = mAuth.getCurrentUser().getUid();

            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("users/" + uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DBHelper.setUser(snapshot.getValue(User.class));
                    fragment = new BooksFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            DatabaseReference connectedReference = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DBHelper.setConnected(snapshot.getValue(Boolean.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


        }

        bottomNavView = findViewById(R.id.navigation);
        bottomNavView.setOnNavigationItemSelectedListener(
                (MenuItem menuItem) -> {
                    fragment = null;
                    menuItem.setChecked(true);
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.settings_item) {
                        fragment = new SettingsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    } else if (itemId == R.id.library_books) {
                        fragment = BooksFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    } else if (itemId == R.id.loaned_books) {
                        fragment = LoanFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    } else if (itemId == R.id.logout_item) {
                        try {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("logout", "yes");
                            startActivity(intent);
                        } catch (Error error) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                        }
                    }

                    return false;
                });
    }

    //We override default behaviour of back button, to set the list of books if we press back on other fragments,
    //and exit the app if we are already there.
    @Override
    public void onBackPressed() {
        if (fragment.getClass().equals(BooksFragment.class)) {
            super.onBackPressed();
        } else {
            fragment = new BooksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            bottomNavView.getMenu().getItem(0).setChecked(true);
        }
    }
}