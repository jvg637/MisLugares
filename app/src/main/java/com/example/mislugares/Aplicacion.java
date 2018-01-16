package com.example.mislugares;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by jvg63 on 13/01/2018.
 */

public class Aplicacion extends Application {
    private FirebaseAuth auth;


    @Override
    public void onCreate() {
        super.onCreate();

        auth = FirebaseAuth.getInstance();

    }

    public FirebaseAuth getAuth() {
        return auth;
    }

}
