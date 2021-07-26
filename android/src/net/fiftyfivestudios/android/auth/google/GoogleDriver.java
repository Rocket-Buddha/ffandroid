package net.fiftyfivestudios.android.auth.google;


import android.app.Activity;
import android.content.Intent;

import com.godot.game.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.Task;

public class GoogleDriver implements net.fiftyfivestudios.android.spi.GoogleAuthConnector {

    // Instancia del Singleton.
    private static GoogleDriver ourInstance;
    // Atributo que referencia la actividad en la que esta corriendo el juego.
    private Activity activity;
    //
    private GoogleSignInAccount googleAccount;
    // Atributos para manejar la sesion de google.
    private GoogleSignInClient googleSignInClient;


    private GoogleDriver(Activity activity) {

        this.activity = activity;


        // Instacio las opciciones de google para dar de alta el Google SingIn.
        GoogleSignInOptions signInOption = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                // If you are using Snapshots add the Drive scope.
                .requestScopes(Drive.SCOPE_APPFOLDER)
                // If you need a server side auth code, request it here.
                .requestScopes(Games.SCOPE_GAMES)
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        // Instancio el GoogleSingInClient.
        googleSignInClient = GoogleSignIn.getClient(activity, signInOption);
    }

    public static synchronized GoogleDriver getInstance(Activity activity) {
        if (ourInstance == null) {
            ourInstance = new GoogleDriver(activity);
            return ourInstance;
        }
        return ourInstance;
    }

    private boolean isSignedIn() {
        if (GoogleSignIn.getLastSignedInAccount(activity) != null) {
            googleAccount = GoogleSignIn.getLastSignedInAccount(activity);
            return true;
        } else return false;
    }

    @Override
    public synchronized Integer signInGoogle() {
        if (!isSignedIn()) {

            Intent signInIntent = googleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            return 0;
        } else {
            // Ya te encuentras logueado.
            return 1;
        }
    }


    @Override
    public synchronized Integer signOutGoogle() {
        if (isSignedIn()) {
            Task<Void> completedTask = googleSignInClient.signOut();
            // La tarea esta completa pero no sabemos si termino satisfactoriamente.
            // Igualmente chequiamos nuvamente.
            if (completedTask.isComplete()) {
                if (completedTask.isSuccessful()) {
                    // Seteo la cuenta en null nuevamente.
                    googleAccount = null;
                    return 0;
                } else {
                    // Fallo el signout.
                    return 1;
                }
            } else {

                return 2;
            }
        } else {
            return 3;
        }
    }

    @Override
    public GoogleSignInAccount getGoogleAccount() {
        return googleAccount;
    }

    @Override
    public Integer handleOnMainActivityResultGoogleSignIn(Intent data) {
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Cargo la cuenta.
            googleAccount = completedTask.getResult(ApiException.class);
            Games.getGamesClient(activity, googleAccount)
                    .setViewForPopups(activity.findViewById(android.R.id.content));

            return 0;
        } catch (ApiException e) {
            // Seteo en nullo por si logueo varias veces.
            googleAccount = null;
            return 1;
        }
    }

}



