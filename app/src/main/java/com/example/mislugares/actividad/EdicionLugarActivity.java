package com.example.mislugares.actividad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mislugares.almacenamiento.LugaresAsinc;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.R;
import com.example.mislugares.modelo.TipoLugar;
import com.example.mislugares.fragment.SelectorFragment;
import com.google.firebase.auth.FirebaseAuth;

public class EdicionLugarActivity extends AppCompatActivity {
    private long id;
    //    private long _id;
    private String _id;
    private Lugar lugar;
    private EditText nombre;
    private Spinner tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
//        _id = extras.getLong("_id", -1);
        _id = extras.getString("_id", null);
//        if (_id!=-1) {
        if (_id != null) {
//            lugar = MainActivity.lugares.elemento((int) _id);
            lugar = new Lugar();
        } else {
//            lugar = SelectorFragment.adaptador.lugarPosicion((int) id);
            lugar = SelectorFragment.getAdaptador().getItem((int) id);
            // Firebase Database
//            _id = SelectorFragment.adaptador.getRef((int) id).getKey();
            _id = SelectorFragment.getAdaptador().getKey((int) id);
        }
        nombre = (EditText) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        direccion = (EditText) findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());
        telefono = (EditText) findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));
        url = (EditText) findViewById(R.id.url);
        url.setText(lugar.getUrl());
        comentario = (EditText) findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
        tipo = (Spinner) findViewById(R.id.tipo);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_lugar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_cancelar:
                if (getIntent().getExtras().getBoolean("nuevo", false)) {
//                    MainActivity.lugares.borrar((int) id);
                    MainActivity.lugares.borrar(_id);
                }
                finish();
                return true;
            case R.id.accion_guardar:
                lugar.setNombre(nombre.getText().toString());
                lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
                lugar.setDireccion(direccion.getText().toString());
                lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
                lugar.setUrl(url.getText().toString());
                lugar.setComentario(comentario.getText().toString());
//                if (_id == -1) {
//                    _id = SelectorFragment.adaptador.idPosicion((int) id);
//                }
//                MainActivity.lugares.actualiza((int) _id, lugar);
                if (id == -1) {
                    lugar.setCreador(FirebaseAuth.getInstance().getUid());
                }
                MainActivity.lugares.actualiza(_id, lugar, new LugaresAsinc.EscuchadorActualiza() {
                    @Override
                    public void onRespuesta(boolean estado) {
                        if (!estado) {
                            Toast.makeText(EdicionLugarActivity.this, "Permisos insuficiones por las reglas", Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                        }
                    }
                });
//                SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
//                if (id != -1) {
//                    SelectorFragment.adaptador.notifyItemChanged((int) id);
//                } else {
//                    SelectorFragment.adaptador.notifyDataSetChanged();
//                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
