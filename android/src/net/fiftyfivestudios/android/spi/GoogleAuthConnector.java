package net.fiftyfivestudios.android.spi;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * Created by alusi on 1/31/18.
 */

public interface GoogleAuthConnector {
    // Identifacor de result para SignIn de google.
    Integer GOOGLE_SIGN_IN = 111002;

    Integer signInGoogle();

    GoogleSignInAccount getGoogleAccount();

    Integer handleOnMainActivityResultGoogleSignIn(Intent data);

    Integer signOutGoogle();

}
