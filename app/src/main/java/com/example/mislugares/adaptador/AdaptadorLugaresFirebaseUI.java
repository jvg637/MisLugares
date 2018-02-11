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

    public AdaptadorLugaresFirebaseUI(@NonNull FirebaseRecyclerOptions<Lugar> opciones, DatabaseReference valoraciones) {
        super(opciones);
        referenceValoraciones = valoraciones;

    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new AdaptadorLugares.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorLugares.ViewHolder holder, int position, @NonNull Lugar lugar) {
        AdaptadorLugares.personalizaVista(holder, lugar);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }


    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getKey();
    }



}