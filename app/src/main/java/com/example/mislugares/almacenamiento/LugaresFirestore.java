package com.example.mislugares.almacenamiento;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mislugares.modelo.Lugar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by jvg63 on 26/01/2018.
 */
public class LugaresFirestore implements LugaresAsinc {
    private final static String NODO_LUGARES = "lugares";
    private CollectionReference lugares;

    public LugaresFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        nodo = database.getReference().child(NODO_LUGARES);
        lugares = db.collection(NODO_LUGARES);
    }

    public void elemento(String id, final EscuchadorElemento escuchador) {
        lugares.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    escuchador.onRespuesta(task.getResult().toObject(Lugar.class));
                } else {
                    Log.e("Firebase", "Error al leer", task.getException());
                    escuchador.onRespuesta(null);
                }
            }
        });
    }


    public void anyade(Lugar lugar) {
        lugares.add(lugar); // lugares.document().set(lugar);
    }

    public String nuevo() {
        return lugares.document().getId();
    }

    public void borrar(String id) {
        lugares.document(id).delete();
    }

    public void actualiza(String id, Lugar lugar, final EscuchadorActualiza actualiza) {
        lugares.document(id).set(lugar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.getException()!=null) {
                    Log.d("LugaresFirestore", "Solo el si el creador es el mismo y esta la información en el creador se actualziara el sitio");
                    actualiza.onRespuesta(false);
                }
                else {
                    actualiza.onRespuesta(true);
                }
            }
        });
    }

    public void tamanyo(final EscuchadorTamanyo escuchador) {
        lugares.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    escuchador.onRespuesta(task.getResult().size());
                } else {
                    Log.e("Firebase", "Error al leer", task.getException());
                    escuchador.onRespuesta(-1);
                }
            }
        });
    }
}