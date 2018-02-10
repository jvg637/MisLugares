package com.example.mislugares.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mislugares.actividad.VistaLugarActivity;
import com.example.mislugares.almacenamiento.ValoracionesFirestore;
import com.example.mislugares.utilidades.DialogoSelectorFecha;
import com.example.mislugares.utilidades.DialogoSelectorHora;
import com.example.mislugares.actividad.EdicionLugarActivity;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.almacenamiento.LugaresAsinc;
import com.example.mislugares.utilidades.PermisosUtilidades;
import com.example.mislugares.R;
import com.example.mislugares.modelo.Usuario;
import com.example.mislugares.actividad.MainActivity;
import com.example.mislugares.utilidades.Preferencias;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VistaLugarFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private long id;
    private Lugar lugar;
    //private ImageView imageView;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;
    private Uri uriFoto;
    private View v;
    private ViewGroup contenedorMsg;
    private View lblValoracion;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.vista_lugar, contenedor, false);
        contenedorMsg = vista.findViewById(R.id.scrollView1);
        setHasOptionsMenu(true);

        lblValoracion = vista.findViewById(R.id.lblValoracion);
        LinearLayout pUrl = (LinearLayout) vista.findViewById(R.id.barra_url);
        pUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pgWeb(null);
            }
        });
        LinearLayout pTlf = (LinearLayout) vista.findViewById(R.id.barra_telefono);
        pTlf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                llamadaTelefono(null);
            }
        });
        LinearLayout pMapa = (LinearLayout) vista.findViewById(R.id.barra_direccion);
        pMapa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verMapa(null);
            }
        });
        ImageView iconoFoto = (ImageView) vista.findViewById(R.id.camara);
        iconoFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                tomarFoto(null);
            }
        });
        ImageView iconoGaleria = (ImageView) vista.findViewById(R.id.galeria);
        iconoGaleria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                galeria(null);
            }
        });
        ImageView iconoBorra = (ImageView) vista.findViewById(R.id.eliminarFoto);
        iconoBorra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                eliminarFoto(null);
            }
        });
        ImageView iconoHora = (ImageView) vista.findViewById(R.id.icono_hora);
        iconoHora.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarHora();
            }
        });
        ImageView iconoFecha = (ImageView) vista.findViewById(R.id.icono_fecha);
        iconoFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFecha();
            }
        });
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id", -1);
            if (id != -1) {
                actualizarVistas(id);
            }
        }
    }

    public void actualizarVistas(final long id) {
        //lugar = MainActivity.lugares.elemento((int) id);
        //lugar = SelectorFragment.adaptador.lugarPosicion((int) id);
        this.id = id;
//        lugar = SelectorFragment.adaptador.lugarPosicion((int) id);
        lugar = SelectorFragment.getAdaptador().getItem((int) id);
        if (lugar != null) {

            TextView nombre = (TextView) v.findViewById(R.id.nombre);
            nombre.setText(lugar.getNombre());
            ImageView logo_tipo = (ImageView) v.findViewById(R.id.logo_tipo);
            logo_tipo.setImageResource(lugar.getTipo().getRecurso());
            TextView tipo = (TextView) v.findViewById(R.id.tipo);
            tipo.setText(lugar.getTipo().getTexto());

            if (lugar.getDireccion().isEmpty()) {
                v.findViewById(R.id.barra_direccion).setVisibility(View.GONE);
            } else {
                TextView direccion = (TextView) v.findViewById(R.id.direccion);
                direccion.setText(lugar.getDireccion());
                direccion.setVisibility(View.VISIBLE);
                v.findViewById(R.id.barra_direccion).setVisibility(View.VISIBLE);
            }
            if (lugar.getTelefono() == 0) {
                v.findViewById(R.id.barra_telefono).setVisibility(View.GONE);
            } else {
                TextView telefono = (TextView) v.findViewById(R.id.telefono);
                telefono.setText(Integer.toString(lugar.getTelefono()));
                v.findViewById(R.id.barra_telefono).setVisibility(View.VISIBLE);
            }
            if (lugar.getUrl().isEmpty()) {
                v.findViewById(R.id.barra_url).setVisibility(View.GONE);
            } else {
                TextView url = (TextView) v.findViewById(R.id.url);
                url.setText(lugar.getUrl());
                url.setVisibility(View.VISIBLE);
                v.findViewById(R.id.barra_url).setVisibility(View.VISIBLE);
            }
            if (lugar.getComentario().isEmpty()) {
                v.findViewById(R.id.barra_comentario).setVisibility(View.GONE);
            } else {
                TextView comentario = (TextView) v.findViewById(R.id.comentario);
                comentario.setText(lugar.getComentario());
                v.findViewById(R.id.barra_comentario).setVisibility(View.VISIBLE);
            }
            TextView fecha = (TextView) v.findViewById(R.id.fecha);
            fecha.setText(DateFormat.getDateInstance().format(
                    new Date(lugar.getFecha())));
            TextView hora = (TextView) v.findViewById(R.id.hora);
            hora.setText(DateFormat.getTimeInstance().format(
                    new Date(lugar.getFecha())));
            final RatingBar valoracion = (RatingBar) v.findViewById(R.id.valoracion);
            valoracion.setOnRatingBarChangeListener(null);

            // FIREBASE DATABASE
            Preferencias pref = Preferencias.getInstance();
            pref.inicializa(getContext());

            if (!pref.usarFirestore()) {


                MainActivity.lugares.getValoracionUsuario(SelectorFragment.getAdaptador().getKey((int) id), FirebaseAuth.getInstance().getCurrentUser().getUid(), new LugaresAsinc.EscuchadorValoracionUsuario() {
                    @Override
                    public void onRespuesta(float valoracionUsuario) {
                        if (valoracionUsuario != -1) {
                            valoracion.setRating(valoracionUsuario);
                            primeraValoracion(false);
                        } else {
                            valoracion.setRating(0);
                            primeraValoracion(true);
                        }

                        //            valoracion.setRating(lugar.getValoracion());
                        valoracion.setOnRatingBarChangeListener(
                                new RatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(RatingBar ratingBar,
                                                                float valor, boolean fromUser) {
//                            lugar.setValoracion(valor);
                                        primeraValoracion(false);
                                        String _id = SelectorFragment.getAdaptador().getKey((int) id);
                                        String usuario = FirebaseAuth.getInstance().getUid();
                                        // FIREBASE DB
                                        Usuario.guardarValoracionUsuario(usuario, _id, valor);
//                            actualizaLugar();
                                    }
                                });
                    }
                });
            } else {
                valoracion.setEnabled(true);
                final String _id = SelectorFragment.getAdaptador().getKey((int) id);
                final String usuario = FirebaseAuth.getInstance().getUid();
                ValoracionesFirestore.leerValoracion(_id, usuario, new ValoracionesFirestore.EscuchadorValoracion() {
                    @Override
                    public void onNoExiste() {
                        this.onRespuesta(0.0);
                        primeraValoracion(true);
                    }

                    @Override
                    public void onRespuesta(Double valor) {
                        valoracion.setOnRatingBarChangeListener(null);
                        valoracion.setRating(valor.floatValue());
                        valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
//                                ValoracionesFirestore.guardarValoracion(_id, usuario, (double) valor);
                                primeraValoracion(false);
                                ValoracionesFirestore.guardarValoracionYRecalcular(_id, usuario, valor);

                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        primeraValoracion(false);
                        this.onRespuesta(0.0);
                        valoracion.setEnabled(false);
                        Toast.makeText(VistaLugarFragment.this.getContext(), "No se puede valorar un lugar que haya creado", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
        }
    }

    private void primeraValoracion(boolean primera) {
        if (primera) {
            lblValoracion.setVisibility(View.VISIBLE);
        } else {
            lblValoracion.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vista_lugar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                lanzarEdicionLugar(id);
                return true;
            case R.id.accion_borrar:
//                int _id = SelectorFragment.adaptador.idPosicion((int) id);
//                borrarLugar((int) _id);
                borrarLugar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void mensaje(String mensaje) {
        Snackbar.make(contenedorMsg, mensaje, Snackbar.LENGTH_LONG).show();
    }

    //    public void borrarLugar(final int id) {
    public void borrarLugar() {
        if (lugar.getCreador() != null && !lugar.getCreador().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            mensaje("Sólo el creador del lugar puede realizar el borrado");
        else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Borrado de lugar")
                    .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // FIREBASE DATABASE
//                            String _id = SelectorFragment.adaptador.getRef((int) id).getKey();
                            // FIREBASE FIRESTORE
                            String _id = SelectorFragment.getAdaptador().getKey((int) id);
                            MainActivity.lugares.borrar(_id);
//                        SelectorFragment.adaptador.setCursor(
//                                MainActivity.lugares.extraeCursor());
//                        SelectorFragment.adaptador.notifyDataSetChanged();
                            SelectorFragment selectorFragment = (SelectorFragment) getActivity().
                                    getSupportFragmentManager().findFragmentById(R.id.selector_fragment);
                            if (selectorFragment == null) {
                                getActivity().finish();
                            } else {
                                ((MainActivity) getActivity()).muestraLugar(0);
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }

    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    public void lanzarEdicionLugar(final long id) {
        Intent i = new Intent(getActivity(), EdicionLugarActivity.class);
        i.putExtra("id", id);
        startActivityForResult(i, RESULTADO_EDITAR);
    }

    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }

    public static final int SOLICITUD_PERMISO_LECTURA = 0;

    ImageView lastImageView;
    String lastUri;

    protected void ponerFoto(ImageView imageView, String uri) {

        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            if (uri.startsWith("content://com.example.mislugares/") ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                            READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imageView.setImageBitmap(reduceBitmap(getActivity(), uri, 1024, 1024));
            } else {
                lastImageView = imageView;
                lastUri = uri;
                PermisosUtilidades.solicitarPermisoFragment(Manifest.permission.
                        READ_EXTERNAL_STORAGE, "Sin permiso de lectura no es posible " +
                        "mostrar fotos de memoria externa", SOLICITUD_PERMISO_LECTURA, this);
            }
        } else {
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_LECTURA) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ponerFoto(lastImageView, lastUri);
            } else {
                ponerFoto(lastImageView, null);
            }
        }
    }

    public static Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto) {
        try {
            InputStream input = null;
            Uri u = Uri.parse(uri);
            if (u.getScheme().equals("http") || u.getScheme().equals("https")) {
                input = new URL(uri).openStream();
            } else {
                input = contexto.getContentResolver().openInputStream(u);
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = (int) Math.max(Math.ceil(options.outWidth / maxAncho), Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso de imagen no encontrado", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Toast.makeText(contexto, "Error accediendo a imagen", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }
//    public static Bitmap reduceBitmap(Context contexto, String uri,
//                                      int maxAncho, int maxAlto) {
//        try {
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(contexto.getContentResolver()
//                    .openInputStream(Uri.parse(uri)), null, options);
//            options.inSampleSize = (int) Math.max(
//                    Math.ceil(options.outWidth / maxAncho),
//                    Math.ceil(options.outHeight / maxAlto));
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeStream(contexto.getContentResolver()
//                    .openInputStream(Uri.parse(uri)), null, options);
//        } catch (FileNotFoundException e) {
//            Toast.makeText(contexto, "Fichero/recurso no encontrado",
//                    Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//            return null;
//        }
//    }

    public void tomarFoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File file = null;
            try {
                file = File.createTempFile(
                        "img_" + (System.currentTimeMillis() / 1000),       // nombre
                        ".jpg",                                             // extensión
                        //Environment.getExternalStoragePublicDirectory("")
                        getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)); // directorio
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (file != null) {
                uriFoto = FileProvider.getUriForFile(getActivity(),
                        "com.example.mislugares",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(intent, RESULTADO_FOTO);
            }
        }
    }

    public void tomarFoto2(View view) {
        /////////////////////
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ///////////////////////////
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uriFoto = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + File.separator
                        + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto((ImageView) v.findViewById(R.id.foto), ""); //null);
        actualizaLugar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(id);
            /*View s = v.findViewById(R.id.scrollView1);
            if (s!=null) {
                s.invalidate();
            }*/
            //v.findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                lugar.setFoto(data.getDataString());
                ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
                actualizaLugar();
            } else {
                Toast.makeText(getActivity(), "Error carfando foto", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK
                    && lugar != null && uriFoto != null) {
                lugar.setFoto(uriFoto.toString());
                ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
                actualizaLugar();
            } else {
                Toast.makeText(getActivity(), "Error capturando foto", Toast.LENGTH_LONG).show();
            }
        }
    }

    void actualizaLugar() {
//        int _id = SelectorFragment.adaptador.idPosicion((int) id);
//        MainActivity.lugares.actualiza(_id, lugar);
//        SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
//        SelectorFragment.adaptador.notifyItemChanged((int) id);
//        SelectorFragment.adaptador.notifyDataSetChanged();
        // FIREBASE DATABASE
//        String _id = SelectorFragment.adaptador.getRef((int) id).getKey();
        // FIREBASE FIRESTORE
        String _id = SelectorFragment.getAdaptador().getKey((int) id);
        MainActivity.lugares.actualiza(_id, lugar, new LugaresAsinc.EscuchadorActualiza() {
            @Override
            public void onRespuesta(boolean estado) {

            }
        });
    }

    public void cambiarHora() {
        DialogoSelectorHora dialogo = new DialogoSelectorHora();
        dialogo.setOnTimeSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogo.setArguments(args);
        dialogo.show(getActivity().getSupportFragmentManager(), "selectorHora");
    }

    @Override
    public void onTimeSet(TimePicker vista, int hora, int minuto) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(lugar.getFecha());
        calendario.set(Calendar.HOUR_OF_DAY, hora);
        calendario.set(Calendar.MINUTE, minuto);
        lugar.setFecha(calendario.getTimeInMillis());
        actualizaLugar();
        TextView tHora = (TextView) getView().findViewById(R.id.hora);
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm",
                java.util.Locale.getDefault());
        tHora.setText(formato.format(new Date(lugar.getFecha())));
    }

    public void cambiarFecha() {
        DialogoSelectorFecha dialogo = new DialogoSelectorFecha();
        dialogo.setOnDateSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogo.setArguments(args);
        dialogo.show(getActivity().getSupportFragmentManager(), "selectorFecha");
    }

    @Override
    public void onDateSet(DatePicker view, int anyo, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(lugar.getFecha());
        calendario.set(Calendar.YEAR, anyo);
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        lugar.setFecha(calendario.getTimeInMillis());
        actualizaLugar();
        TextView tFecha = (TextView) getView().findViewById(R.id.fecha);
        DateFormat formato = DateFormat.getDateInstance();
        tFecha.setText(formato.format(new Date(lugar.getFecha())));
    }
}