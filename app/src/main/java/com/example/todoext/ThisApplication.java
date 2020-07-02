package com.example.todoext;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class ThisApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}