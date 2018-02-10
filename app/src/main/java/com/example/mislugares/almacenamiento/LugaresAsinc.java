package com.example.mislugares.almacenamiento;

import com.example.mislugares.modelo.Lugar;

/**
 * Created by jvg63 on 26/01/2018.
 */

public interface LugaresAsinc {
    void getValoracionUsuario(String lugar, String uid, EscuchadorValoracionUsuario escuchador );

    void getValoracionMedia(String idLugar, Lugar lugar, EscuchadorValorcionMedia escuchador);


    interface EscuchadorValoracionUsuario{
        void onRespuesta(float valoracion);
    }

    interface EscuchadorActualiza{
        void onRespuesta(boolean estado);
    }


    interface EscuchadorElemento {
        void onRespuesta(Lugar lugar);
    }

    interface EscuchadorTamanyo {
        void onRespuesta(long tamanyo);
    }
    interface EscuchadorValorcionMedia {
        void onRespuesta(float valoracion);
    }




//    void elemento(String id, EscuchadorElemento escuchador);

    void anyade(Lugar lugar);

    String nuevo();

    void borrar(String id);

    void actualiza(String id, Lugar lugar, EscuchadorActualiza actualiza);

    void tamanyo(EscuchadorTamanyo escuchador);
}