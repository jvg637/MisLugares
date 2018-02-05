package com.example.mislugares.utilidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jvg63 on 28/01/2018.
 */

public class Preferencias {
    private static final Preferencias INSTANCIA = new Preferencias();
    private SharedPreferences pref;
    public final static int SELECCION_TODOS = 0;
    public final static int SELECCION_MIOS = 1;
    public final static int SELECCION_TIPO = 2;

    public static Preferencias getInstance() {
        return INSTANCIA;
    }

    private Preferencias() {
    }

    public void inicializa(Context contexto) {
        if (pref == null)
            pref = PreferenceManager.getDefaultSharedPreferences(contexto);
    }

    public int criterioSeleccion() {
        return Integer.parseInt(pref.getString("seleccion", "0"));
    }

    public String tipoSeleccion() {
        return (pref.getString("tipo_seleccion", "BAR"));
    }

    public String criterioOrdenacion() {
        return (pref.getString("orden", "valoracion"));
    }

    public int maximoMostrar() {
        return Integer.parseInt(pref.getString("maximo", "50"));
    }

    public boolean usarFirestore() {
        return (pref.getBoolean("firestore", true));
    }

    public boolean usarFirebaseUI() {
        return (pref.getBoolean("firebaseUI", true));
    }
}