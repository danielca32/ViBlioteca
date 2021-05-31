package com.danielcastro.viblioteca;

import com.google.firebase.database.FirebaseDatabase;

public class OfflinePersistenceUnit extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

