package com.example.mislugares.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
                new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        ponerAdaptador();

    }

    public void ponerAdaptador() {

        Preferencias pref = Preferencias.getInstance();
        pref.inicializa(getContext());
//        RecyclerView.LayoutManager layoutManager = layoutManager.setAutoMeasureEnabled(true);
        //Quitar si da problemas
//        adaptador = new AdaptadorLugaresBD(getContext(),
//                MainActivity.lugares, MainActivity.lugares.extraeCursor());
//        adaptador.setOnItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) getActivity()).muestraLugar(
//                        recyclerView.getChildAdapterPosition(v));
//                /*Intent i = new Intent(getContext(), VistaLugarActivity.class);
//                i.putExtra("id", (long)
//                        recyclerView.getChildAdapterPosition(v));
//                startActivity(i);*/
//            }
//        });
//        recyclerView.setAdapter(adaptador);

        // FIREBASE UI DATABASE
//        Query query = FirebaseDatabase.getInstance().getReference().child("lugares").limitToLast(pref.maximoMostrar());
//        switch (pref.criterioSeleccion()) {
//            case SELECCION_MIOS:
//                query = query.orderByChild("creador").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                break;
//            case SELECCION_TIPO:
//                query = query.orderByChild("tipo").equalTo(pref.tipoSeleccion());
//                break;
//            default:
//                query = query.orderByChild(pref.criterioOrdenacion());
//                break;
//        }
//        FirebaseRecyclerOptions<Lugar> opciones = new FirebaseRecyclerOptions.Builder<Lugar>().setQuery(query, Lugar.class).build();
//        adaptador = new AdaptadorLugaresFirebaseUI(opciones, FirebaseDatabase.getInstance().getReference().child("valoraciones"));

        // FIREBASE DATABASE
//        adaptador = new AdaptadorLugaresFirebase(getActivity(), query, FirebaseDatabase.getInstance().getReference().child("valoraciones"));

        // FIREBASE FIRESTORE UI
//            com.google.firebase.firestore.Query query = FirebaseFirestore.getInstance().collection("lugares")
//                    .orderBy(pref.criterioOrdenacion(), com.google.firebase.firestore.Query.Direction.DESCENDING).limit(pref.maximoMostrar());
//
//
//            switch (pref.criterioSeleccion()) {
//                case SELECCION_MIOS:
//                    query = query.whereEqualTo("creador", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    break;
//                case SELECCION_TIPO:
//                    query = query.whereEqualTo("tipo", pref.tipoSeleccion());
//                    break;
//            }
//            FirestoreRecyclerOptions<Lugar> opciones = new FirestoreRecyclerOptions.Builder<Lugar>().setQuery(query, Lugar.class).build();
//
//            adaptador = new AdaptadorLugaresFirestoreUI(opciones);
        // FIREBASE FIRESTORE
//        com.google.firebase.firestore.Query query = FirebaseFirestore.getInstance().collection("lugares").orderBy(pref.criterioOrdenacion()).limit(pref.maximoMostrar();
//        adaptador = new AdaptadorLugaresFirestore(getActivity(), query);
//        getAdaptador().setOnItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) getActivity()).muestraLugar(
//                        recyclerView.getChildAdapterPosition(v));
//                //Intent i = new Intent(getContext(), VistaLugarActivity.class);
//                //i.putExtra("id", (long)
//                //        recyclerView.getChildAdapterPosition(v));
//                //startActivity(i);
//            }
//        });

        if (pref.usarFirestore()) {
            com.google.firebase.firestore.Query query = FirebaseFirestore.getInstance().collection("lugares").orderBy(pref.criterioOrdenacion()).limit(pref.maximoMostrar());
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

    private Query getQueryDatabase(Preferencias pref) {
        // FIREBASE UI DATABASE
        Query query = FirebaseDatabase.getInstance().getReference().child("lugares").limitToLast(pref.maximoMostrar());
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

        return query;
    }

    @NonNull
    private com.google.firebase.firestore.Query getQueryFireStore(Preferencias pref) {
        com.google.firebase.firestore.Query queryFirestore = FirebaseFirestore.getInstance().collection("lugares")
                .orderBy(pref.criterioOrdenacion(), com.google.firebase.firestore.Query.Direction.DESCENDING).limit(pref.maximoMostrar());

        switch (pref.criterioSeleccion()) {
            case SELECCION_MIOS:
                queryFirestore = queryFirestore.whereEqualTo("creador", FirebaseAuth.getInstance().getCurrentUser().getUid());
                break;
            case SELECCION_TIPO:
                queryFirestore = queryFirestore.whereEqualTo("tipo", pref.tipoSeleccion());
                break;
        }
        return queryFirestore;
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