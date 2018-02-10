package com.example.mislugares.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.mislugares.utilidades.PermisosUtilidades;
import com.example.mislugares.R;
import com.example.mislugares.actividad.CustomLoginActivity;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.io.IOException;

/**
 * Created by jvg63 on 13/01/2018.
 */

public class UsuarioFragment extends Fragment {
    private static final int PROVIDER_FACEBOOK = 0;
    private static final int PROVIDER_GMAIL = 1;
    private static final int PROVIDER_PASSWORD = 2;
    private static final int PROVIDER_PHONE = 3;
    private static final int PROVIDER_ANONIMO = 4;
    private static final int PROVIDER_TWITTER = 5;
    private EditText nombre;
    private TextInputLayout tilNombre;
    private NetworkImageView fotoUsuario;
    private EditText correo;
    private TextInputLayout tilCorreo;
    private EditText telefono;
    private TextInputLayout tilTelefono;
    private EditText password;
    private TextInputLayout tilPassword;


    private ViewGroup contenedorVista;

    private String[] imagenesPerfil = {
            "",
            "https://mis-lugares-81fd4.firebaseapp.com/img/foto_perfil1.jpg",
            "https://mis-lugares-81fd4.firebaseapp.com/img/foto_perfil2.jpg",
            "https://mis-lugares-81fd4.firebaseapp.com/img/foto_perfil3.jpg",
    };

    private String[] imagenesPerfilSpinner = {
            "Sel. Imagen ....",
            "foto_perfil1.jpg",
            "foto_perfil2.jpg",
            "foto_perfil3.jpg",
    };

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
                startActivityForResult(intent, VistaLugarFragment.RESULTADO_FOTO);
            }
        }
    }

    public void eliminarFoto(View view) {
        ponerFoto((ImageView) fotoUsuario, ""); //null);

    }

    private Uri uriFoto;
    private ImageLoader lectorImagenes;
    private Spinner spinner_foto_pefil;
    private Button cerrarSesion;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_usuario, contenedor, false);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        nombre = (EditText) vista.findViewById(R.id.nombre);
        tilNombre = (TextInputLayout) vista.findViewById(R.id.til_nombre);
        nombre.setText(usuario.getDisplayName());

        password = (EditText) vista.findViewById(R.id.password);
        tilPassword = (TextInputLayout) vista.findViewById(R.id.til_password);

        password.setText(passwordEmail);
        if (isEmailProvider()) {
            tilPassword.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
        } else {
            tilPassword.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
        }

        contenedorVista = (ViewGroup) vista.findViewById(R.id.contenedor);

        correo = (EditText) vista.findViewById(R.id.correo);
        tilCorreo = (TextInputLayout) vista.findViewById(R.id.til_correo);
        correo.setText(usuario.getEmail());

        if (moreThanOneProvider() || isEmailProvider() || isFaceBookProvider() || isGoogleProvider()) {
            tilCorreo.setEnabled(false);
            correo.setEnabled(false);
        } else {
            tilCorreo.setEnabled(true);
            correo.setEnabled(true);
        }


        TextView proveedor = (TextView) vista.findViewById(R.id.proveedor);
        proveedor.setText(usuario.getProviders().toString());

        telefono = (EditText) vista.findViewById(R.id.telefono);
        tilTelefono = (TextInputLayout) vista.findViewById(R.id.til_telefono);
        telefono.setText(usuario.getPhoneNumber());

        TextView uid = (TextView) vista.findViewById(R.id.uid);
        uid.setText(usuario.getUid());


        cerrarSesion = (Button) vista.findViewById(R.id.btn_cerrar_sesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isFaceBookProvider()) {
                    LoginManager.getInstance().logOut();
                }


                AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        Intent i = new Intent(getActivity(), CustomLoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        getActivity().finish();
                    }
                });
            }
        });
        spinner_foto_pefil = (Spinner) vista.findViewById(R.id.spinner_foto_perfil);
        ArrayAdapter adapter = new ArrayAdapter(vista.getContext(),
                android.R.layout.simple_spinner_item, imagenesPerfilSpinner);
        spinner_foto_pefil.setAdapter(adapter);
        spinner_foto_pefil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                mensaje("Seleccionada:" + i);
                if (pos > 0) {
                    fotoUsuario.setImageUrl(imagenesPerfil[pos], lectorImagenes);
                } else {
                    if (urlImagen != null) {

                        fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        ImageView iconoFoto = (ImageView) vista.findViewById(R.id.camara);
//        iconoFoto.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                tomarFoto(null);
//            }
//        });
//        ImageView iconoGaleria = (ImageView) vista.findViewById(R.id.galeria);
//        iconoGaleria.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                galeria(null);
//            }
//        });
//        ImageView iconoBorra = (ImageView) vista.findViewById(R.id.eliminarFoto);
//        iconoBorra.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                eliminarFoto(null);
//            }
//        });

        Button grabarPerfil = (Button) vista.findViewById(R.id.btn_grabar_perfil);
        grabarPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (verificaCampos()) {
                    final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre.getText().toString())
                            .setPhotoUri(
                                    (spinner_foto_pefil.getSelectedItemPosition() == 0
                                            ? usuario.getPhotoUrl()
                                            : Uri.parse(imagenesPerfil[spinner_foto_pefil.getSelectedItemPosition()])))
                            .build();

                    usuario.updateProfile(perfil).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.e("MisLugares", "Actualización Perfil Incorrecta!");
                                recargaUsuario(usuario, false);
                            } else {
                                if (isEmailProvider()) {
                                    usuario.updateEmail(correo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("MisLugares", "Actualización Correo Incorrecta!" + " " + task.getException().getMessage());
                                            } else {
                                                usuario.updatePassword(password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                                                mensaje("Debe volver a autentificarse");
                                                                recargaUsuario(usuario, true);

//                                                                AuthCredential credential = EmailAuthProvider.getCredential(correo.getText().toString(), oldPassword);
//                                                                usuario.reauthenticate(credential);
                                                            }
                                                            Log.e("MisLugares", "Actualización Password Incorrecta!" + " " + task.getException().getMessage());
                                                        } else {
                                                            recargaUsuario(usuario, false);
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                                } else {
                                    recargaUsuario(usuario, false);
                                }
                            }
                        }

                    });
                }
            }
        });

        lectorImagenes = getImageLoader();


        // Foto de usuario
        urlImagen = usuario.getPhotoUrl();
        fotoUsuario = (NetworkImageView) vista.findViewById(R.id.imagen);


        Button unirCuenta = (Button) vista.findViewById(R.id.btn_unir_cuenta);
        unirCuenta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CustomLoginActivity.class);
                i.putExtra("unificar", true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                getActivity().finish();
            }
        });

        return vista;
    }

    // Foto de usuario
    private Uri urlImagen;

    private void recargaUsuario(FirebaseUser usuario, final boolean goToLogin) {
        usuario.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mensaje("Perfil Actualizado!");

                if (goToLogin) {
                    cerrarSesion.callOnClick();
                }
            }
        });
    }

    public static String passwordEmail = "";

    private boolean verificaCampos() {
        String nombreStr = nombre.getText().toString();
        String correoStr = correo.getText().toString();
        String telefonoStr = telefono.getText().toString();
        String contraseñaStr = password.getText().toString();

        tilNombre.setError("");
        tilCorreo.setError("");
        tilTelefono.setError("");
        tilPassword.setError("");

        boolean validaEmail = false;
        boolean validaContraseña = false;
//
        if (!moreThanOneProvider()) {
            if (isAnonimoProvider()) {
                validaEmail = true;
                validaContraseña = false;
            } else if (isFaceBookProvider() || isGoogleProvider()) {
                validaEmail = false;
                validaContraseña = false;
            } else if (isTwitterProvider()) {
                validaEmail = true;
                validaContraseña = false;
            } else if (isEmailProvider()) {
                validaEmail = false;
                validaContraseña = true;
            } else if (isPhoneProvider()) {
                validaEmail = true;
                validaContraseña = false;
            }
        } else {

        }
        if (nombreStr.isEmpty()) {
            tilNombre.setError("Introduce un nombre");
        } else if (validaEmail && !correoStr.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else if (validaContraseña && contraseñaStr.isEmpty()) {
            tilPassword.setError("Introduce una contraseña");
        } else if (validaContraseña && contraseñaStr.length() < 6) {
            tilPassword.setError("Longitud contraseña <6");
        } else if (validaContraseña && !contraseñaStr.matches(".*[0-9].*")) {
            tilPassword.setError("Ha de contener un número");
        } else if (validaContraseña && !contraseñaStr.matches(".*[A-Z].*")) {
            tilPassword.setError("Ha de contener una letra mayúscula");
        } else {
            return true;
        }
        return false;
    }


    @NonNull
    private ImageLoader getImageLoader() {
        // Inicialización Volley  (Hacer solo una vez en Singleton o Applicaction)
        RequestQueue colaPeticiones = Volley.newRequestQueue(getActivity());
        return new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });
    }

    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, VistaLugarFragment.RESULTADO_GALERIA);
    }


    private void mensaje(String mensaje) {
        Snackbar.make(contenedorVista, mensaje, Snackbar.LENGTH_LONG).show();
    }

    protected void ponerFoto(ImageView imageView, String uri) {

        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            if (uri.startsWith("content://com.example.mislugares/") ||
                    ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.
                            READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imageView.setImageBitmap(VistaLugarFragment.reduceBitmap(getActivity(), uri, 1024, 1024));
            } else {
                PermisosUtilidades.solicitarPermisoFragment(android.Manifest.permission.
                        READ_EXTERNAL_STORAGE, "Sin permiso de lectura no es posible " +
                        "mostrar fotos de memoria externa", VistaLugarFragment.SOLICITUD_PERMISO_LECTURA, UsuarioFragment.this);
            }
        } else {
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VistaLugarFragment.RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {

                ponerFoto((ImageView) fotoUsuario, data.getDataString());

            } else {
                Toast.makeText(getActivity(), "Error carfando foto", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == VistaLugarFragment.RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK
                    && uriFoto != null) {
                ponerFoto((ImageView) fotoUsuario, uriFoto.toString());
            } else {
                Toast.makeText(getActivity(), "Error capturando foto", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isGoogleProvider() {
        return providerCorreoVerificadoOtroAcceso() == PROVIDER_GMAIL;
    }

    private boolean isTwitterProvider() {
        return providerCorreoVerificadoOtroAcceso() == PROVIDER_TWITTER;
    }

    private boolean isEmailProvider() {
        return providerCorreoVerificadoOtroAcceso() == PROVIDER_PASSWORD;
    }

    private boolean moreThanOneProvider() {
        return providerCorreoVerificadoOtroAcceso() == -1;
    }

    private boolean isPhoneProvider() {
        return providerCorreoVerificadoOtroAcceso() == PROVIDER_PHONE;
    }

    private boolean isAnonimoProvider() {
        return providerCorreoVerificadoOtroAcceso() == PROVIDER_ANONIMO;
    }

    private boolean isFaceBookProvider() {
        return providerCorreoVerificadoOtroAcceso() == PROVIDER_FACEBOOK;
    }

    private int providerCorreoVerificadoOtroAcceso() {
        int contadorProviders = 0;
        int idProvider = PROVIDER_ANONIMO;

        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().getProviders() != null) {
            for (String user : FirebaseAuth.getInstance().getCurrentUser().getProviders()) {
                Log.d("xx_xx_provider_info", user);
                if (user.equals("facebook.com")) {
                    //For linked facebook account
                    Log.d("xx_xx_provider_info", "User is signed in with Facebook");
                    contadorProviders++;
                    idProvider = PROVIDER_FACEBOOK;

                } else if (user.equals("google.com")) {
                    //For linked Google account
                    Log.d("xx_xx_provider_info", "User is signed in with Google");
                    contadorProviders++;
                    idProvider = PROVIDER_GMAIL;
                } else if (user.equals("twitter.com")) {
                    //For linked Google account
                    Log.d("xx_xx_provider_info", "User is signed in with Twitter");
                    contadorProviders++;
                    idProvider = PROVIDER_TWITTER;
                } else if (user.equals("password")) {
                    //For linked Google account
                    Log.d("xx_xx_provider_info", "User is signed in with Password");
                    contadorProviders++;
                    idProvider = PROVIDER_PASSWORD;
                } else if (user.equals("phone")) {
                    //For linked Google account
                    Log.d("xx_xx_provider_info", "User is signed in with Phone");
                    idProvider = PROVIDER_PHONE;
                    contadorProviders++;
                } else {
                    Log.d("xx_xx_provider_info", "User is signed in with Else");
                    contadorProviders++;
                }
            }
            if (contadorProviders > 1)
                idProvider = -1;
        }
        return idProvider;
    }
}