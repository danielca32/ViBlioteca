package com.danielcastro.viblioteca;

import com.google.firebase.database.FirebaseDatabase;

/**
 * @author dcast
 *
 * This class tells the FireBase manager to set persistence enabled
 * and allow the application to keep working offline in device storage instead of memory.
 *
 * On the manifest we tell this subclass of android.app.Application to instantie it before any other.
 * We do this because telling FireBase to set persistence enabled twice without fully restarting
 * the application will crash it intentionally.
 */

public class OfflinePersistenceUnit extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

