package com.example.mislugares.modelo;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by jvg63 on 26/01/2018.
 */

public class Usuario {
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public long getInicioSesion() {
        return inicioSesion;
    }

    public void setInicioSesion(long inicioSesion) {
        this.inicioSesion = inicioSesion;
    }

    private String nombre;
    private String correo;
    private long inicioSesion;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, long inicioSesion) {
        this.nombre = nombre;
        this.correo = correo;
        this.inicioSesion = inicioSesion;
    }

    public Usuario(String nombre, String correo) {
        this(nombre, correo, System.currentTimeMillis());
    }

    public static void guardarUsuario(final FirebaseUser user) {
        Usuario usuario = new Usuario(user.getDisplayName(), user.getEmail());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("usuarios/" + user.getUid()).setValue(usuario);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid()).set(usuario);
    }

    public static void guardarValoracionUsuario(final String user, final String idLugar, final float valoracion) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("valoraciones/" + idLugar + "/" + user).setValue(valoracion);

    }
}
