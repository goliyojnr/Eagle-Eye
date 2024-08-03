package com.cholera.eagleeye;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class DatabaseInitializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
