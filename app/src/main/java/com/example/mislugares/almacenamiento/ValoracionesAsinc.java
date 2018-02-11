package com.example.mislugares.almacenamiento;

/**
 * Created by jvg63 on 11/02/2018.
 */

public interface ValoracionesAsinc {

    public interface EscuchadorValoracion {
        void onRespuesta(Double valor);

        void onNoExiste();

        void onError(Exception e);
    }
    public void guardarValoracionYRecalcular(String lugar, String usuario, float valor);
    public void leerValoracion(String lugar, String usuario, final ValoracionesFirebase.EscuchadorValoracion escuchador);
}
