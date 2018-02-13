package com.example.mislugares.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.adaptador.AdaptadorLugaresFirebase;
import com.example.mislugares.adaptador.AdaptadorLugaresFirebaseUI;
import com.example.mislugares.adaptador.AdaptadorLugaresFirestore;
import com.example.mislugares.adaptador.AdaptadorLugaresFirestoreUI;
import com.example.mislugares.adaptador.AdaptadorLugaresInterface;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.utilidades.Preferencias;
import com.example.mislugares.R;
import com.example.mislugares.actividad.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.mislugares.utilidades.Preferencias.SELECCION_MIOS;
import static com.example.mislugares.utilidades.Preferencias.SELECCION_TIPO;

/**
 * Created by Jesús Tomás on 19/04/2017.
 */

public class SelectorFragment extends Fragment {
    private static RecyclerView recyclerView;
    //    public static AdaptadorLugaresBD adaptador;
//    public static AdaptadorLugaresFirebaseUI adaptador;
//    public static AdaptadorLugaresFirebase adaptador;
//    public static AdaptadorLugaresFirestoreUI adaptador;
//    public static AdaptadorLugaresFirestore adaptador;

    public static RecyclerView.Adapter adaptador;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_view);
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(false);
//                layoutManager.setAutoMeasureEnabled(  true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        ponerAdaptador();

    }

    public void ponerAdaptador() {

        Preferencias pref = Preferencias.getInstance();
        pref.inicializa(getContext());
        //Quitar si da problemas

        if (getAdaptador()!=null)
            getAdaptador().stopListening();

        if (pref.usarFirestore()) {
            com.google.firebase.firestore.Query query = FirebaseFirestore.getInstance().collection("lugares").limit(pref.maximoMostrar());
            if (!pref.tipoSeleccion().equals("tipo")) {
                query = query.orderBy(pref.criterioOrdenacion());
            }
            switch (pref.criterioSeleccion()) {
                case SELECCION_MIOS:
                    query = query.whereEqualTo("creador", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    break;
                case SELECCION_TIPO:
                    query = query.whereEqualTo("tipo", pref.tipoSeleccion());
                    break;
            }
            if (pref.usarFirebaseUI()) {
                FirestoreRecyclerOptions<Lugar> options = new FirestoreRecyclerOptions.Builder<Lugar>().setQuery(query, Lugar.class).build();
                adaptador = new AdaptadorLugaresFirestoreUI(options);
            } else {
                adaptador = new AdaptadorLugaresFirestore(getContext(), query);
            }
        } else {
            if (pref.usarFirebaseUI()) {
                com.google.firebase.database.Query query = FirebaseDatabase.getInstance().getReference().child("lugares").limitToLast(pref.maximoMostrar());
                switch (pref.criterioSeleccion()) {
                    case SELECCION_MIOS:
                        query = query.orderByChild("creador").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        break;
                    case SELECCION_TIPO:
                        query = query.orderByChild("tipo").equalTo(pref.tipoSeleccion());
                        break;
                    default:
                        query = query.orderByChild(pref.criterioOrdenacion());
                        break;
                }
                FirebaseRecyclerOptions<Lugar> options = new FirebaseRecyclerOptions.Builder<Lugar>().setQuery(query, Lugar.class).build();
                adaptador = new AdaptadorLugaresFirebaseUI(options, FirebaseDatabase.getInstance().getReference("valoraciones"));
            } else {
                adaptador = new AdaptadorLugaresFirebase((MainActivity) getActivity(), FirebaseDatabase.getInstance().getReference("lugares"), FirebaseDatabase.getInstance().getReference("valoraciones"));
            }
        }

        getAdaptador().setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).muestraLugar(recyclerView.getChildAdapterPosition(v));
            }
        });

        recyclerView.setAdapter(adaptador);
        getAdaptador().startListening();

    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        adaptador.startListening();
//    }

    @Override
    public void onDestroy() {
        getAdaptador().stopListening();
        super.onDestroy();
    }

    public static AdaptadorLugaresInterface getAdaptador() {
        return (AdaptadorLugaresInterface) adaptador;
    }
}