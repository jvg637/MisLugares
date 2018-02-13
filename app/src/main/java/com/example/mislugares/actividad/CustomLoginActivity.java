package com.example.mislugares.actividad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mislugares.R;
import com.example.mislugares.almacenamiento.LugaresAsinc;
import com.example.mislugares.fragment.UsuarioFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static com.example.mislugares.modelo.Usuario.guardarUsuario;

public class CustomLoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth auth;
    private String correo = "";
    private String contraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    private Button btnAnonimo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null)
            auth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    initialize();
                }
            });
        else
            initialize();


    }

    private void initialize() {
        //        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();;
//        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

//        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_custom_login);
        etCorreo = (EditText) findViewById(R.id.correo);
        etContraseña = (EditText) findViewById(R.id.contraseña);
        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_contraseña);
        contenedor = (ViewGroup) findViewById(R.id.contenedor);
        btnAnonimo = (Button) findViewById(R.id.anonimo);
        dialogo = new ProgressDialog(this);
//        dialogo.setCancelable(false);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        //Facebook
        callbackManager = CallbackManager.Factory.create();
        btnFacebook = (LoginButton) findViewById(R.id.facebook);
        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookAuth(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                mensaje("Cancelada autentificación con facebook");
                errorLogin();
            }


            @Override
            public void onError(FacebookException error) {
                errorLogin();
                mensaje(error.getLocalizedMessage());
            }
        });


        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);


        btnTwitter = (TwitterLoginButton) findViewById(R.id.twitter);
        btnTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                twitterAuth(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                errorLogin();
                mensaje(exception.getLocalizedMessage());
            }
        });

        btnTwitter.setEnabled(true);
        unificar = getIntent().getBooleanExtra("unificar", false);

        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);

        muestraOcultaAnonima();
        verificaSiUsuarioValidado();
    }

    private void muestraOcultaAnonima() {
        if (unificar) {
            btnAnonimo.setVisibility(View.INVISIBLE);
        } else {
            btnAnonimo.setVisibility(View.VISIBLE);

        }
    }

    private void verificaSiUsuarioValidado() {
        if (!unificar && FirebaseAuth.getInstance().getCurrentUser() != null) {
            guardarUsuario(auth.getCurrentUser(), new LugaresAsinc.EscuchadorActualiza() {
                @Override
                public void onRespuesta(boolean estado) {
                    if (estado) {
                        Intent i = new Intent(CustomLoginActivity.this, MainActivity.class);
                        UsuarioFragment.passwordEmail = contraseña;
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        if (dialogo != null)
                            dialogo.dismiss();
                        startActivity(i);
                        finish();
                    } else {
                        mensaje("Error grabando Usuario en BBDD");
                    }
                }
            });

        }
    }

    public void inicioSesion(View v) {
        if (verificaCampos()) {
            dialogo.show();

            auth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        verificaSiUsuarioValidado();
                    } else {
                        errorLogin();
                        mensaje(task.getException().getLocalizedMessage());
                    }
                }
            });

        }
    }

    public void registroCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            if (unificar) {
                AuthCredential credential = EmailAuthProvider.getCredential(correo, contraseña);
                unificarCon(credential);
            } else {
                auth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verificaSiUsuarioValidado();
                        } else {
                            errorLogin();
                            mensaje(task.getException().getLocalizedMessage());
                        }
                    }
                });
            }
        }
    }

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();
        tilCorreo.setError("");
        tilContraseña.setError("");
        if (correo.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else if (contraseña.isEmpty()) {
            tilContraseña.setError("Introduce una contraseña");
        } else if (contraseña.length() < 6) {
            tilContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            tilContraseña.setError("Ha de contener un número");
        } else if (!contraseña.matches(".*[A-Z].*")) {
            tilContraseña.setError("Ha de contener una letra mayúscula");
        } else {
            return true;
        }
        return false;
    }

    public void firebaseUI(View v) {
        UsuarioFragment.passwordEmail = "";
        startActivity(new Intent(this, LoginActivity.class));
    }

    private static final int RC_GOOGLE_SIGN_IN = 123;
    private GoogleApiClient googleApiClient;

    public void autentificarGoogle(View v) {
        dialogo.show();
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    googleAuth(result.getSignInAccount());
                } else {
                    errorLogin();
                    mensaje("Error de autentificación con Google");
                }
            } else {
                errorLogin();
            }
        } else if (requestCode == btnFacebook.getRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            btnTwitter.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void facebookAuth(AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        if (unificar) {
            unificarCon(credential);
        } else {
            auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        errorLogin();
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            LoginManager.getInstance().logOut();
                        }
                        mensaje(task.getException().getLocalizedMessage());
                    } else {
                        verificaSiUsuarioValidado();
                    }
                }
            });
        }
    }

    public void autentificarFacebook(View v) {
        dialogo.show();
    }

    private void googleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        if (unificar) {
            unificarCon(credential);
        } else {
            auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        errorLogin();
                        mensaje(task.getException().getLocalizedMessage());
                    } else {
                        verificaSiUsuarioValidado();
                    }
                }

            });
        }
    }

    private void unificarCon(AuthCredential credential) {

        auth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    unificar = false;
                    verificaSiUsuarioValidado();
                } else {
                    Log.w("MisLugares", "Error en linkWithCredential", task.getException());
                    mensaje("Error al unificar cuentas.");
                    errorLogin();
                    muestraOcultaAnonima();
                }
            }
        });
    }

    private void errorLogin() {
//        unificar = false;
        if (dialogo != null)
            dialogo.dismiss();
//        if (auth.getCurrentUser() != null)
//            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                }
//            });
//
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mensaje(getString(R.string.error_connection_failed));
    }

    private CallbackManager callbackManager;
    private LoginButton btnFacebook;

    private TwitterLoginButton btnTwitter;

    private void twitterAuth(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);

        if (unificar) {
            unificarCon(credential);
        } else {
            auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        mensaje(task.getException().getLocalizedMessage());
                        errorLogin();
                    } else {
                        verificaSiUsuarioValidado();
                    }
                }
            });
        }
    }


    public void autentificaciónAnónima(View v) {
        dialogo.show();
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    verificaSiUsuarioValidado();
                } else {
                    errorLogin();
                    Log.w("MisLugares", "Error en signInAnonymously", task.getException());
                    mensaje("ERROR al intentarentrar de forma anónima");
                }
            }
        });
    }

    private boolean unificar;

    public void reestablecerContraseña(View v) {
        correo = etCorreo.getText().toString();
        tilCorreo.setError("");
        if (correo.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else {
            dialogo.show();
            auth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    errorLogin();
                    if (task.isSuccessful()) {
                        mensaje("Verifica tu correo para cambiar contraseña.");
                    } else {
                        mensaje("ERROR al mandar correo para cambiar contraseña");
                    }
                }
            });
        }
    }
}
