/*-----------------------------------------------------------------------*/
/*   AuthFF.java                                                         */
/*-----------------------------------------------------------------------*/
/*                       This file is part of:                           */
/*        Android FireBase Notification Dispatcher Godot Module          */
/*                     http://www.55studios.net/                         */
/*-----------------------------------------------------------------------*/
/* Copyright (c) 2018 Andres Julian Lusi.                                */
/*                                                                       */
/* Permission is hereby granted, free of charge, to any person obtaining */
/* a copy of this software and associated documentation files (the       */
/* "Software"), to deal in the Software without restriction, including   */
/* without limitation the rights to use, copy, modify, merge, publish,   */
/* distribute, sublicense, and/or sell copies of the Software, and to    */
/* permit persons to whom the Software is furnished to do so, subject to */
/* the following conditions:                                             */
/*                                                                       */
/* The above copyright notice and this permission notice shall be        */
/* included in all copies or substantial portions of the Software.       */
/*                                                                       */
/* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,       */
/* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF    */
/* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.*/
/* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY  */
/* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,  */
/* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE     */
/* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                */
/*-----------------------------------------------------------------------*/

package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;


import net.fiftyfivestudios.android.auth.google.GoogleDriver;
import net.fiftyfivestudios.android.spi.GoogleAuthConnector;

/**
 * Clase Singleton AUTHFF cuya responsabilidad es ser la Fachada del modulo,
 * y a la vez encapsular toda la logica de autenticacion con Google y FirebaseConnector.
 * La clase no es publica por que solo es utilizada en el contexto
 * del paquete org.godotengine.godot.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
class AuthFF extends Godot.SingletonBase {

    // Tag de godot para logging.
    private final static String TAG = "godot";
    // Atributo que referencia la actividad en la que esta corriendo el juego.
    private final Activity activity;
    //
    GoogleAuthConnector googleAuthDriver;

    // Atributo que referencia la intancia a la que debe hacer los callbacks.
    private Integer instanceId;

    // Atributos que referencia a los callbacks en Godot.
    private String googleSignInCallback;
    private String googleSignInFailedCallback;
    private String googleSignOutCallback;
    private String googleSignOutFailedCallback;
    private String googleGetAccountCallback;

    /**
     * Constructor privado de la clase que solo es invocado desde el metodo initialize.
     *
     * @param paramActivity Actividad principal en donde esta corriendo el juego.
     */
    private AuthFF(Activity paramActivity) {
        // Registro los metodos que voy a consumir desde godot.
        registerClass("AuthFF", new String[]{
                "setup",
                "sign_in_google",
                "sign_out_google",
                "get_google_account",
        });
        // Instancio los atributos.
        activity = paramActivity;
        // Instacio las opciciones de google para dar de alta el Google SingIn.
    }

    /**
     * Metodo invocado una sola vez desde Globals cuando se solicita el Singleton desde GDScript.
     * No debemos preocuparnos de manejar la logica del Singleton ya que esta, esta encapsulada dentro
     * del metodo Globals.get_singleton, verificando un hash map, al ver que no hay value para el objeto, la instancia, una unica vez.
     *
     * @param activity Actividad principal en donde esta corriendo el juego.
     * @return Devuelve el modulo instanciado.
     */
    static protected Godot.SingletonBase initialize(Activity activity) {
        return new AuthFF(activity);
    }

    /**
     * Metodo invocado para hacer el setup del modulo.
     *
     * @param paramInstanceId                   Intancia a la que debo hacer los callbacks.
     * @param paramGoogleSignInCallback         Metodo de al que llama cuando hace el SignIn de Google.
     * @param paramGoogleSignInFailedCallback   Metodo al que llama cuando falla el SignIn de Google.
     * @param paramGoogleSignOutCallback        Metodo que llama cuando hace el SignOut de Google.
     * @param paramGoogleSignOutFailedCallback  Metodo al que llama cuando falla el SignOut de Google.
     */
    protected void setup(final int paramInstanceId, final String paramGoogleSignInCallback,
                         final String paramGoogleSignInFailedCallback,
                         final String paramGoogleSignOutCallback,
                         final String paramGoogleSignOutFailedCallback,
                         final String paramGoogleGetAccountCallback) {
        // Instancio los atributos.
        instanceId = paramInstanceId;
        // Instancio los callbacks a Godot.
        googleSignInCallback = paramGoogleSignInCallback;
        googleSignInFailedCallback = paramGoogleSignInFailedCallback;
        googleSignOutCallback = paramGoogleSignOutCallback;
        googleSignOutFailedCallback = paramGoogleSignOutFailedCallback;
        googleGetAccountCallback = paramGoogleGetAccountCallback;
        // Configuro el driver.
        googleAuthDriver = GoogleDriver.getInstance(activity);
    }

    /**
     * Metodo invocado para hacer el SignIn en Google.
     */
    protected void sign_in_google() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instanceId != null) {
                    //
                    Integer result = googleAuthDriver.signInGoogle();
                    //
                    switch (result) {
                        case 0:
                            Log.d(TAG, "AUTHFF: Logueando cuenta de Google...");
                            break;
                        case 1:
                            Log.d(TAG, "AUTHFF: Ya te encuntras logueado."
                                    + googleAuthDriver.getGoogleAccount().getEmail());
                            GodotLib.calldeferred(instanceId, googleSignInCallback, new Object[]{});
                            break;
                    }
                } else {
                    Log.d(TAG, "AUTHFF: Necesitas hacer el setup del modulo de godot primero.");
                }
            }
        });
    }

    /**
     * Metodo invocado para hacer el SignOut en Google.
     */
    protected void sign_out_google() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instanceId != null) {
                    //
                    Integer result = googleAuthDriver.signOutGoogle();
                    //
                    switch (result) {
                        case 0:
                            // Logueo.
                            Log.d(TAG, "AUTHFF: Google SignOut completado con exito.");
                            // Hago callback.
                            GodotLib.calldeferred(instanceId, googleSignOutCallback, new Object[]{});
                            break;
                        case 1:
                            // Logueo.
                            Log.d(TAG, "AUTHFF: Fallo el SignOut de Google.");
                            // Hago callback.
                            GodotLib.calldeferred(instanceId, googleSignOutFailedCallback, new Object[]{});
                            break;
                        case 2:
                            Log.w(TAG, "AUTHFF: Google SignOut tarea inclompleta... Esto es raro!");
                            GodotLib.calldeferred(instanceId, googleSignOutFailedCallback, new Object[]{});
                            break;
                        case 3:
                            Log.d(TAG, "AUTHFF: No te encuentras logueado.");
                    }
                } else {
                    Log.d(TAG, "AUTHFF: Necesitas hacer el setup del modulo de godot primero.");
                }
            }
        });
    }

    /**
     * Metodo invocado desde Godot.SingletonBase cuando retorna un resultado a la actividad principal.
     *
     * @param requestCode Indentificador de la vuelta.
     * @param resultCode  Resultado.
     * @param data        Informacion del resultado.
     */
    @Override
    protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        //
        if (requestCode == GoogleAuthConnector.GOOGLE_SIGN_IN) {
            Integer result = googleAuthDriver.handleOnMainActivityResultGoogleSignIn(data);

            switch (result) {
                case 0:
                    Log.d(TAG, "AUTHFF: Loging de Google realizado con: "
                            + googleAuthDriver.getGoogleAccount().getEmail());
                    GodotLib.calldeferred(instanceId, googleSignInCallback,
                            new Object[]{});
                    break;
                default:
                    Log.d(TAG, "AUTHFF: Loging de Google fallo.");
                    GodotLib.calldeferred(instanceId, googleSignInFailedCallback,
                            new Object[]{});

            }
        }
    }

    /**
     * Metodo invocado para obtener un JSON con los datos de la cuenta de Google.
     *
     * @return Cuenta de Google logueada en formato JSON.
     */
    protected void get_google_account() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (googleAuthDriver != null) {
                    GoogleSignInAccount googleSignInAccount = googleAuthDriver.getGoogleAccount();
                    if (googleSignInAccount != null) {
                        //
                        String accountJSON = (new Gson()).toJson(googleSignInAccount);
                        //
                        Log.d(TAG, "AUTHFF: Enviando a godot los datos de la cuenta de Google.");
                        GodotLib.calldeferred(instanceId, googleGetAccountCallback,
                                new Object[]{accountJSON});
                    } else
                        Log.d(TAG, "AUTHFF: La cuenta de Google es nula, por favor inicie sesion primero.");
                } else {
                    Log.d(TAG, "AUTHFF: Necesitas hacer el setup del modulo de godot primero.");
                }
            }
        });
    }
}
