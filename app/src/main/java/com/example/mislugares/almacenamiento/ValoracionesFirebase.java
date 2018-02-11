package com.example.mislugares.almacenamiento;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mislugares.modelo.Lugar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

import static com.example.mislugares.almacenamiento.LugaresFirebase.NODO_LUGARES;

/**
 * Created by jvg63 on 05/02/2018.
 */

public class ValoracionesFirebase implements ValoracionesAsinc {
    private final static String NODO_VALORACIONES = "valoraciones";


    //    public static void guardarValoracion(String lugar, String usuario, Double valor) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> datos = new HashMap<>();
//        datos.put("valoracion", valor);
//        db.collection("lugares").document(lugar).collection("valoraciones").document(usuario).set(datos);
//    }
    public void guardarValoracion(String idLugar, String user, Double valoracion) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("valoraciones/" + idLugar + "/" + user).setValue(valoracion);

    }

//    @Override
//    public void leerValoracion(final String lugar, String uid, final ValoracionesFirebase.EscuchadorValoracion escuchador) {
//        DatabaseReference nodoValoraciones = FirebaseDatabase.getInstance().getReference().child(NODO_VALORACIONES);
//        nodoValoraciones.child(lugar).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists() && dataSnapshot.getValue() != null)
//                    escuchador.onRespuesta(dataSnapshot.getValue(Float.class));
//                else
//                    escuchador.onRespuesta(-1);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.e("Firebase", "Error al leer.", error.toException());
//                escuchador.onRespuesta(-1);
//            }
//        });
//    }


    public void leerValoracion(String lugar, String usuario, final EscuchadorValoracion escuchador) {
        DatabaseReference nodoValoraciones = FirebaseDatabase.getInstance().getReference().child(NODO_VALORACIONES);
        nodoValoraciones.child(lugar).child(usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    escuchador.onNoExiste();
                } else {
                    double val = dataSnapshot.getValue(Double.class);
                    escuchador.onRespuesta(val);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Mis Lugares", "No se puede leer valoraciones", databaseError.toException());
                escuchador.onError(databaseError.toException());
            }
        });
    }


    @Override
    public void guardarValoracionYRecalcular(final String lugar, final String usuario, final float nuevaVal) {
        leerValoracion(lugar, usuario, new EscuchadorValoracion() {
            @Override
            public void onRespuesta(Double viejaVal) {
                actualizarValoracionMedia(lugar, usuario, viejaVal, nuevaVal);
            }

            @Override
            public void onNoExiste() {
                actualizarValoracionMedia(lugar, usuario, Double.NaN, nuevaVal);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void actualizarValoracionMedia(final String lugarId, final String usuario, final double viejaVal, final double nuevaVal) {
        final DatabaseReference nodoLugar = FirebaseDatabase.getInstance().getReference().child(NODO_LUGARES).child(lugarId);
        nodoLugar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    Lugar lugar = dataSnapshot.getValue(Lugar.class);
                    double media = lugar.getValoracion();

                    long nValor = lugar.getN_valoraciones();

                    double nuevaMedia = nuevaMedia(media, nValor, viejaVal, nuevaVal);
                    if (Double.isNaN(viejaVal)) nValor++;

                    lugar.setValoracion((float) nuevaMedia);
                    lugar.setN_valoraciones(nValor);

                    nodoLugar.setValue(lugar);

                    guardarValoracion(lugarId, usuario, nuevaVal);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Mis Lugares", "ERROR al leer", databaseError.toException());
            }
        });


    }


    private Double nuevaMedia(double media, long nValoraciones, double viejaVal, double nuevaVal) {
        if (nValoraciones == 0) { //No existe ninguna valoración

            return nuevaVal;
        } else if (Double.isNaN(viejaVal)) { //No existe valoración anterior
            return (nValoraciones * media + nuevaVal) / (nValoraciones + 1);
        } else {   //El usuario cambia su valoración
            return (nValoraciones * media - viejaVal + nuevaVal) / nValoraciones;
        }
    }

}