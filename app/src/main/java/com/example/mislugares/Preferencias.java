package com.example.mislugares;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jvg63 on 28/01/2018.
 */

public class Preferencias {
    public static boolean usarFirestore(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return (pref.getBoolean("firestore", true));
    }

    public static boolean usarFirebaseUI(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return (pref.getBoolean("firebaseUI", true));
    }
}
