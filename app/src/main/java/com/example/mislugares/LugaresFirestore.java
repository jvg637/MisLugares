package com.example.mislugares;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

    public void actualiza(String id, Lugar lugar) {
        lugares.document(id).set(lugar);
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