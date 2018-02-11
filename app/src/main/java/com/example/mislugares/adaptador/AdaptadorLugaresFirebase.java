package com.example.mislugares.adaptador;

/**
 * Created by jvg63 on 27/01/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.almacenamiento.LugaresAsinc;
import com.example.mislugares.R;
import com.example.mislugares.actividad.MainActivity;
import com.example.mislugares.modelo.Lugar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdaptadorLugaresFirebase extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> implements ChildEventListener, AdaptadorLugaresInterface {
    private Query reference;
    private DatabaseReference referenceValoraciones;
    private ArrayList<String> keys;
    private ArrayList<DataSnapshot> items;
    private LayoutInflater inflador;
    private View.OnClickListener onClickListener;

    public AdaptadorLugaresFirebase(Context contexto, Query ref, DatabaseReference refValoraciones) {
        keys = new ArrayList<String>();
        items = new ArrayList<DataSnapshot>();
        reference = ref;
        referenceValoraciones = refValoraciones;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elemento_lista, null);
        v.setOnClickListener(onClickListener);
        return new AdaptadorLugares.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptadorLugares.ViewHolder holder, int posicion) {
        Lugar lugar = getItem(posicion);
        AdaptadorLugares.personalizaVista(holder, lugar);
    }

    public Lugar getItem(int pos) {
        if (pos != -1)
            return items.get(pos).getValue(Lugar.class);
        else return null;
    }

    public DatabaseReference getRef(int pos) {
        return items.get(pos).getRef();
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        items.add(dataSnapshot);
        keys.add(dataSnapshot.getKey());
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        int index = keys.indexOf(key);
        if (index != -1) {
            items.set(index, dataSnapshot);
            notifyItemChanged(index, dataSnapshot.getValue(Lugar.class));
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        int index = keys.indexOf(key);
        if (index != -1) {
            keys.remove(index);
            items.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    public void startListening() {
        keys = new ArrayList<String>();
        items = new ArrayList<DataSnapshot>();
        reference.addChildEventListener(this);
    }


    public void stopListening() {
        reference.removeEventListener(this);
    }

    public String getKey(int pos) {
        return items.get(pos).getRef().getKey();
    }


}