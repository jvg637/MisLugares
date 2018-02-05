package com.example.mislugares.adaptador;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.almacenamiento.LugaresAsinc;
import com.example.mislugares.R;
import com.example.mislugares.actividad.MainActivity;
import com.example.mislugares.modelo.Lugar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jvg63 on 26/01/2018.
 */

public class AdaptadorLugaresFirebaseUI extends FirebaseRecyclerAdapter<Lugar, AdaptadorLugares.ViewHolder> implements AdaptadorLugaresInterface {

    private DatabaseReference referenceValoraciones;
    protected View.OnClickListener onClickListener;
    private Map<String, Float> valoracionesMedias;

    public AdaptadorLugaresFirebaseUI(@NonNull FirebaseRecyclerOptions<Lugar> opciones, DatabaseReference valoraciones) {
        super(opciones);
        referenceValoraciones = valoraciones;
        valoracionesMedias = new HashMap<>();

    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new AdaptadorLugares.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorLugares.ViewHolder holder, int position, @NonNull Lugar lugar) {
        Float valoracion = valoracionesMedias.get(getKey(position));
        if (valoracion != null) {
            lugar.setValoracion(valoracion);

        } else {
            lugar.setValoracion(0);
        }
        AdaptadorLugares.personalizaVista(holder, lugar);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }


    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getKey();
    }

    public ChildEventListener valoracionesListener =
            new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    getCalificacionMedia(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    getCalificacionMedia(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    getCalificacionMedia(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    getCalificacionMedia(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


    @Override
    public void startListening() {
        super.startListening();
        valoracionesMedias = new HashMap<>();
        referenceValoraciones.addChildEventListener(valoracionesListener);
    }

    @Override
    public void stopListening() {
        referenceValoraciones.removeEventListener(valoracionesListener);
        super.stopListening();

    }


    private void getCalificacionMedia(DataSnapshot dataSnapshot) {
        final String key = dataSnapshot.getKey();
        int pos = getIndexOfKey(key);
        if (pos>=0 && pos < getItemCount()) {
            final Lugar lugar = getItem(getIndexOfKey(key));

            if (lugar != null) {
                if (dataSnapshot.exists()) {
                    MainActivity.lugares.getValoracionMedia(dataSnapshot.getKey(), lugar, new LugaresAsinc.EscuchadorValorcionMedia() {
                        @Override
                        public void onRespuesta(float valoracion) {


                            int index = getIndexOfKey(key);
                            if (index >= 0) {
                                if (valoracionesMedias.containsKey(key)) {
                                    valoracionesMedias.remove(key);
                                }
                                valoracionesMedias.put(key, valoracion);
                                notifyItemChanged(index);
                            }
                        }
                    });
                }
            }
        }
    }

    public int getIndexOfKey(String key) {
        for (int i = 0; i < getItemCount(); i++) {

            if (getRef(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;

    }

}