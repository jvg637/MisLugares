package com.example.mislugares;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Created by jvg63 on 28/01/2018.
 */

public class AdaptadorLugaresFirestoreUI extends FirestoreRecyclerAdapter<Lugar, AdaptadorLugares.ViewHolder> {
    protected View.OnClickListener onClickListener;

    public AdaptadorLugaresFirestoreUI(@NonNull FirestoreRecyclerOptions<Lugar> options) {
        super(options);
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
        return super.getSnapshots().getSnapshot(pos).getId();
    }

}