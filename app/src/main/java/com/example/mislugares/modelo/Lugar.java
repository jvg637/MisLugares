package com.example.mislugares.modelo;

public class Lugar {
    private String nombre;
    private String direccion;
    private GeoPunto posicion;
    private String foto;
    private int telefono;
    private String url;
    private String comentario;
    private long fecha;
    private float valoracion;
    private TipoLugar tipo;
    private long n_valoraciones;

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    private String creador;


    public Lugar(String nombre, String direccion, double longitud,
                 double latitud, int telefono, String url, String comentario,
                 int valoracion) {
        fecha = System.currentTimeMillis();
        posicion = new GeoPunto(longitud, latitud);
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.url = url;
        this.comentario = comentario;
        this.valoracion = valoracion;
    }

    public Lugar() {
        fecha = System.currentTimeMillis();
        posicion = new GeoPunto(0, 0);
        tipo = TipoLugar.OTROS;
        n_valoraciones = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public String toString() {
        return "Lugar{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", posicion=" + posicion +
                ", foto='" + foto + '\'' +
                ", telefono=" + telefono +
                ", url='" + url + '\'' +
                ", comentario='" + comentario + '\'' +
                ", fecha=" + fecha +
                ", valoracion=" + valoracion +
                ", tipo=" + tipo +
                '}';
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public GeoPunto getPosicion() {
        return posicion;
    }

    public void setPosicion(GeoPunto posicion) {
        this.posicion = posicion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public TipoLugar getTipo() {
        return tipo;
    }

    public void setTipo(TipoLugar tipo) {
        this.tipo = tipo;
    }

    public Lugar(String nombre, String direccion, double longitud,
                 double latitud, TipoLugar tipo, int telefono, String url, String comentario,
                 int valoracion) {
        fecha = System.currentTimeMillis();
        posicion = new GeoPunto(longitud, latitud);
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.tipo = tipo;
        this.url = url;
        this.comentario = comentario;
        this.valoracion = valoracion;
    }

    public Lugar(String nombre, String direccion, double longitud,
                 double latitud, TipoLugar tipo, int telefono, String url, String comentario,
                 int valoracion, String foto) {
        fecha = System.currentTimeMillis();
        posicion = new GeoPunto(longitud, latitud);
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.tipo = tipo;
        this.url = url;
        this.comentario = comentario;
        this.valoracion = valoracion;
        this.foto = foto;
    }

    public long getN_valoraciones() {
        return n_valoraciones;
    }

    public void setN_valoraciones(long n_valoraciones) {
        this.n_valoraciones = n_valoraciones;
    }

//    public String getTipo() {
//        if (tipo == null) return null;
//        else return tipo.name();
//    }
//
//    public void setTipo(String nombre) {
//        if (nombre == null) tipo = null;
//        else tipo = TipoLugar.valueOf(nombre);
//    }


}