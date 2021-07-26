/*************************************************************************/
/*  .java                                                     */
/*************************************************************************/
/*                       This file is part of:                           */
/*                 Android FireBase Analitycs Godot Module               */
/*                     http://www.55studios.net/                         */
/*************************************************************************/
/* Copyright (c) 2017 Andrés Julián Lusi.                                */
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
/*************************************************************************/

package org.godotengine.godot;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.godot.game.BuildConfig;
import com.godot.game.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Clase Singleton AnaliticsFF cuya responsabilidad es ser la Fachada del modulo,
 * y a la vez encapsular toda la logica de FireBase Analitycs.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
class FBRemoteConfigFF extends Godot.SingletonBase {

    // Atributo responsable de conocer la actividad.
    // En Android las actividades son las ventanas que interactuan con el usuario.
    private Activity activity;

    private FirebaseApp firebaseApp;

    private FirebaseRemoteConfig firebaseRemoteConfig;

    //private FirebaseRemoteConfigSettings firebaseRemoteConfigSettings;

    /**
     * Constructor privado de la clase que solo es invocado desde el método initialize.
     *
     * @param parameActivity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    private FBRemoteConfigFF(Activity parameActivity) {
        // Registra la Clase y los metodos a ser bindeados.
        super.registerClass("FBRemoteConfigFF", new String[]{
                "get_value"
        });
        // Instancia los atributos.
        this.activity = parameActivity;



        firebaseApp = FirebaseApp.initializeApp(activity);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteConfigs();
        Log.d("godot", "FBRemoteConfigFF: Instanciado con exito!");
    }

    /**
     * Metodo invocado una sola vez desde Globals cuando se solicita el Singleton desde GDScript.
     * No debemos preocuparnos de manejar la logica del Singleton ya que esta, esta encapsulda dentro
     * del metodo Globals.get_singleton, verificando un hash map, al ver que no hay value para el objeto, la instancia, una unica vez.
     *
     * @param activity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @return Retorna un Godot.SingletonBase, particularmente el AnalitycsFF, en este caso.
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    static public Godot.SingletonBase initialize(Activity activity) {
        return new FBRemoteConfigFF(activity);
    }

    /**
     *
     */
    private void fetchRemoteConfigs () {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("godot", "FBRemoteConfigFF: Cargando configuracion remota...");
                long cacheExpiration = 3600;
                if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
                    cacheExpiration = 0;
                }

                firebaseRemoteConfig.fetch(cacheExpiration)
                        .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("godot", "FBRemoteConfigFF: Configuracion recuperada con exito.");
                                    firebaseRemoteConfig.activateFetched();
                                } else {
                                    Log.d("godot", "FBRemoteConfigFF: No se pudo recuperar la configuracion.");
                                }
                            }
                        });
            }
        });
    }

//    public void setDefaultsFile (final String filePath) {
//        if (!isInitialized()) { return; }
//
//        Utils.d("Loading Defaults from file:" + filePath);
//
//        String data = Utils.readFromFile(filePath, activity.getApplicationContext());
//        data = data.replaceAll("\\s+", "");
//
//        setDefaults (data);
//    }
//
//    public void setDefaults(final String defaults) {
//        if (!isInitialized()) { return; }
//
//        Map<String, Object> defaultsMap = Utils.jsonToMap(defaults);
//        Utils.d("RemoteConfig: Setting Default values, " + defaultsMap.toString());
//
//        mFirebaseRemoteConfig.setDefaults(defaultsMap);
//    }

    protected String get_value (final String key) {
        if (!isInitialized()) {
            return "NULL";
        }
        Log.d("godot", "FBRemoteConfigFF: Obteniendo la KEY: " + key + ".");
        return firebaseRemoteConfig.getValue(key).asString();
    }

//    public String getValue (final String key, final String namespace) {
//        if (!isInitialized()) { return "NULL"; }
//
//        Utils.d("Getting Remote config value for { " + key + " : " + namespace + " }");
//        return mFirebaseRemoteConfig.getValue(key, namespace).asString();
//    }

    private boolean isInitialized() {
        if (firebaseApp == null) {
            Log.d("godot", "FBRemoteConfigFF: Error, FireBase Remote config no inicializado.");
            return false;
        }
        return true;
    }


}
