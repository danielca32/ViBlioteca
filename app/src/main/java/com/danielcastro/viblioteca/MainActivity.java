package com.danielcastro.viblioteca;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity{


    //Creating member variable for FirebaseAuth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //NavigationBar
    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BooksFragment()).commit();
        bottomNavView=findViewById(R.id.navigation);
        bottomNavView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener(){@Override
                    public boolean onNavigationItemSelected(MenuItem menuItem){
                    Fragment fragment = null;
                    menuItem.setChecked(true);
                    //TODO Replace for new ones
                    switch (menuItem.getItemId()){
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
                            } catch(Error error) {
                                System.out.println("ERROR!");//TODO Implement proper errors.
                        }
                        break;
                    }

        return false;
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null){
          startActivity(new Intent(this, LoginActivity.class));
        }



    }


}