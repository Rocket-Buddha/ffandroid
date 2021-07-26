/*-----------------------------------------------------------------------*/
/*  GPSFF.java                                                           */
/*-----------------------------------------------------------------------*/
/*                       This file is part of:                           */
/*      Android FireBase Google Play Game Services Godot Module          */
/*                     http://www.55studios.net/                         */
/*-----------------------------------------------------------------------*/
/* Copyright (c) 2017 Andres Julian Lusi.                                */
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
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import net.fiftyfivestudios.android.auth.google.GoogleDriver;

/**
 * Clase Singleton GPSFF cuya responsabilidad es ser la Fachada del modulo,
 * y a la vez encapsular toda la logica de Google Play Game Services. La clase no es publica por que solo
 * es utilizada en el contexto del paquete org.godotengine.godot.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
class GPSFF extends Godot.SingletonBase {

    // Atributo que referencia la actividad en la que esta corriendo el juego.
    private final Activity activity;
    //
    AchievementsClient achievementsClient;
    // Atributo destinado a referenciar la API de Google.
    // Identificador de instancia del objeto al que voy a hacer los callbacks.
    private Integer instanceId;

    /**
     * Constructor privado de la clase que solo es invocado desde el metodo initialize.
     *
     * @param paramActivity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    private GPSFF(Activity paramActivity) {
        registerClass("GPSFF", new String[]{
                "setup",
                "unlock_achievement",
                "increment_achievement",
                "show_achievement_list",
        });
        // Instancio los atributos.
        this.activity = paramActivity;
        //Armo el API Client con su Builder (patron de disenio).

    }

    /**
     * Metodo invocado una sola vez desde Globals cuando se solicita el Singleton desde GDScript.
     * No debemos preocuparnos de manejar la logica del Singleton ya que esta, esta encapsulada dentro
     * del metodo Globals.get_singleton, verificando un hash map, al ver que no hay value para el objeto, la instancia, una unica vez.
     *
     * @param activity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @return Retorna un Godot.SingletonBase, particularmente el GPSFF, en este caso.
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    static public Godot.SingletonBase initialize(Activity activity) {

        return new GPSFF(activity);
    }

    /**
     * Metodo invocado para hacer el set_up, este metodo debe ser invocado antes de utilizar cualquier otro metodo.
     *
     * @param paramInstanceId Especifica el identificador de instancia al que haremos los callbacks.
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    public void setup(final int paramInstanceId) {

        instanceId = paramInstanceId;

        GoogleSignInAccount googleSignInAccount = GoogleDriver.getInstance(activity)
                .getGoogleAccount();

        if(googleSignInAccount != null){
            achievementsClient = Games.getAchievementsClient(activity, googleSignInAccount);
            Log.d("godot", "GPSFF: Setup realizado con exito.");
        }
        else{
            Log.d("godot", "GPSFF: No hay ninguna sesion activa de google.");
        }
    }

    /**
     * Metodo invocado para incrementar un logro incrementable en una medida determinada.
     *
     * @param id        Identificador de logro.
     * @param increment Cantidad de incremento.
     */
    public void increment_achievement(final String id, final int increment) {
        // En otro hilo para no bloquear, incremento el logro y logueo...
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                achievementsClient.increment(id, increment);
                Log.d("godot", "GPSFF: Incrementado logro  '" + id + "' en " + increment + ".");
            }
        });
    }

    /**
     * Metodo invocado para desbloquear un logro.
     *
     * @param id Identificador del logro des-bloqueado.
     */
    public void unlock_achievement(final String id) {
        // Valido que parametro obtenido no sea vacio.
        if (id.length() != 0 && id != null) {

            // En otro hilo para no bloquear, desbloqueo el logro y logueo...
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    achievementsClient.unlockImmediate(id);
                    Log.d("godot", "GPSFF: Logro '" + id + "' desbloqueado.");
                }
            });
            
        } else {
            Log.d("godot", "GPSFF: Logro no valido - Cadena vacia o nula.");
        }
    }

    /**
     * Metodo invocado para traer los achivments.
     *
     */
    public void show_achievement_list() {
        achievementsClient.getAchievementsIntent().addOnCompleteListener(new OnCompleteListener<Intent>() {
            @Override
            public void onComplete(@NonNull Task<Intent> task) {
                if (task.isSuccessful()) {
                    GodotLib.calldeferred(instanceId, "_on_google_play_game_services_request_achievements",
                            new Object[]{task.getResult()});
                } else {
                    GodotLib.calldeferred(instanceId, "_on_google_play_game_services_request_achievements",
                            new Object[]{task});
                }
            }
        });
        Log.d("godot", "GPSFF: Trayendo lista de achievements.");
    }
}