package com.example.mislugares;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by jvg63 on 13/01/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();


        getHash();
    }

    private void getHash() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo("com.example.mislugares", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Audiolibros", "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    private void login() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {


            if (providerCorreoVerificadoOtroAcceso(usuario)) {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {

                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        chooseProvider();
                    }
                });
            }
        } else {
            chooseProvider();
        }
    }

    private void chooseProvider() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.FirebaseUITema)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(), new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                        , new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                ))
                .setIsSmartLockEnabled(false).build(), RC_SIGN_IN);
    }

    private boolean providerCorreoVerificadoOtroAcceso(FirebaseUser usuario) {
//        if (usuario.getProviderId().equals("email")) {
        boolean loginByPassword = false;
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            Log.d("xx_xx_provider_info", user.getProviderId());
            if (user.getProviderId().equals("facebook.com")) {
                //For linked facebook account
                Log.d("xx_xx_provider_info", "User is signed in with Facebook");


            } else if (user.getProviderId().equals("google.com")) {
                //For linked Google account
                Log.d("xx_xx_provider_info", "User is signed in with Google");

            } else if (user.getProviderId().equals("password")) {
                //For linked Google account
                Log.d("xx_xx_provider_info", "User is signed in with Password");
                loginByPassword = true;
            } else {
                Log.d("xx_xx_provider_info", "User is signed in with Else");
            }
        }


        if (loginByPassword) {

            if (usuario.isEmailVerified()) {
                return true;
            } else {
                usuario.sendEmailVerification();
                Toast.makeText(this, "Usuario no ha sidido verificado", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == ResultCodes.OK) {
                login();
//                Se quita porque si se envia login a validar (email) y luego se introduce otro
//                finish();
            } else {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
                    return;
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_LONG).show();
                    return;
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Error desconocido", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}