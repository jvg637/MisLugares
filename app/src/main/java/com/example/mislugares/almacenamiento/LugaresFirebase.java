package com.example.mislugares.almacenamiento;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mislugares.modelo.Lugar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jvg63 on 26/01/2018.
 */

public class LugaresFirebase implements LugaresAsinc {
    private final static String NODO_LUGARES = "lugares";
    private final static String NODO_VALORACIONES = "valoraciones";
    private DatabaseReference nodo;
    private DatabaseReference nodoValoraciones;

    public LugaresFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        nodo = database.getReference().child(NODO_LUGARES);
        nodoValoraciones = database.getReference().child(NODO_VALORACIONES);
    }

    public void elemento(String id, final EscuchadorElemento escuchador) {
        nodo.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lugar lugar = dataSnapshot.getValue(Lugar.class);
                escuchador.onRespuesta(lugar);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al leer.", error.toException());
                escuchador.onRespuesta(null);
            }
        });
    }

    @Override
    public void getValoracionUsuario(final String lugar, String uid, final EscuchadorValoracionUsuario escuchador) {
        nodoValoraciones.child(lugar).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue()!=null)
                    escuchador.onRespuesta(dataSnapshot.getValue(Float.class));
                else
                    escuchador.onRespuesta(-1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al leer.", error.toException());
                escuchador.onRespuesta(-1);
            }
        });
    }

    public void anyade(Lugar lugar) {
        nodo.push().setValue(lugar);
    }

    public String nuevo() {
        return nodo.push().getKey();
    }

    public void borrar(String id) {
        nodo.child(id).setValue(null);
    }

    public void actualiza(String id, Lugar lugar, final EscuchadorActualiza actualiza) {
        nodo.child(id).setValue(lugar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    actualiza.onRespuesta(true);
                } else {
                    actualiza.onRespuesta(false);
                }
            }
        });


    }

    public void tamanyo(final EscuchadorTamanyo escuchador) {
        nodo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Lugar lugar = dataSnapshot.getValue(Lugar.class);
                escuchador.onRespuesta(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error en tamanyo.", error.toException());
                escuchador.onRespuesta(-1);
            }
        });
    }

    @Override
    public void getValoracionMedia(final String idLugar, Lugar lugar, final EscuchadorValorcionMedia escuchador){
        if (idLugar!= null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("valoraciones/" + idLugar).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long childrenCount = dataSnapshot.getChildrenCount();
                    float media = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        media += (float) snapshot.getValue(Float.class);
                    }

                    if (childrenCount > 0)
                        media /= childrenCount;
                    else
                        media = 0;

//                if (lugar.getValoracion() != media) {
//                    lugar.setValoracion(media);

//                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    +                    database.getReference("lugares").child(idLugar).setValue(lugar);
//                }
                    escuchador.onRespuesta(media);
                    Log.d("<MEDIA>", idLugar + ":" + media);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}