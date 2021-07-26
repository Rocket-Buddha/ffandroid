/*************************************************************************/
/*  AnalitycsFF.java                                                     */
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.util.Log;

import net.fiftyfivestudios.android.analytics.FirebaseConnector;
import net.fiftyfivestudios.android.spi.AnalyticsConnector;


/**
 * Clase Singleton AnaliticsFF cuya responsabilidad es ser la Fachada del modulo,
 * y a la vez encapsular toda la logica de FireBase Analitycs.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
class AnalitycsFF extends Godot.SingletonBase {

    // Atributo responsable de conocer la actividad.
    // En Android las actividades son las ventanas que interactuan con el usuario.
    private Activity activity;
    // Atributo destinado a guardar una referencia con el modulo de base de datos.
    private AnalyticsConnector registerDatabase;

    /**
     * Constructor privado de la clase que solo es invocado desde el método initialize.
     *
     * @param parameActivity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    private AnalitycsFF(Activity parameActivity) {
        // Registra la Clase y los metodos a ser bindeados.
        super.registerClass("AnalitycsFF", new String[]{
                "send_event",
                "send_level_won",
                "send_level_lost",
                "send_live_won",
                "send_live_lost",
                "send_pwup_used",
                "send_pwup_obtained",
                "send_world_unlocked",
                "send_level_selected",
                "send_world_selected",
                "send_tutorial_completed"
        });
        // Instancia los atributos.
        this.activity = parameActivity;
        this.registerDatabase = FirebaseConnector.getInstance(parameActivity);
        Log.d("godot", "AnalitycsFF: Instanciado y cargado.");
/**
        try {
            // Llamo al driver concreto de esta manera para mantener desacoplada la libreria,
            // de quien la utiliza.
            Method method = Class.forName("net.fiftyfivestudios.android.analytics.FirebaseConnector")
                   .getMethod("getInstance");
            Object object = parameActivity;
            this.registerDatabase = (AnalyticsConnector) method.invoke(object);


            Log.d("godot", "AnalitycsFF: Instanciado y cargado.");

        } catch (ClassNotFoundException e) {
            Log.d("godot", "AnalitycsFF: Error: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.d("godot", "AnalitycsFF: Error: " + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.d("godot", "AnalitycsFF: Error: " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d("godot", "AnalitycsFF: Error: " + e.getMessage());
        }

**/
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
        return new AnalitycsFF(activity);
    }

    /**
     * Metodo invocado para registrar un evento cualquiera por su nombre (Method to record an event with any name that you need).
     *
     * @param eventName Nombre del evento sin espacios, se puede usar guion bajo. (Name event to record, you do not use space but if you can use hyphen-medium")
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    public void send_event(final String eventName) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendEvent(eventName);
                Log.d("godot", "AnalitycsFF: Evento simple registrado.");
            }
        });
    }

    /**
     * Metodo invocado para registrar el evento el nivel ganado (Method to record event of won level).
     *
     * @param world Numero del mundo jugado informado en formato texto.(String of world played)
     * @param level Numero del nivel ganado informado en formato texto. (String of level won)
     * @param stars Cantidad de estrellas obtenidas en el nivel ganado en formato texto. (Quantity of star obtained)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_level_won(final String world, final String level, final String stars) {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendLevelWon(world, level, stars);
                Log.d("godot", "AnalitycsFF: Nivel ganado registrado.");
            }
            });
    }

    /**
     * Metodo invocado para registrar el evento de nivel perdido (Method to record event of lost level).
     *
     * @param world Numero del mundo jugado.(Number of world played)
     * @param level Numero del nivel ganado. (Number of level won)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_level_lost(final int world, final int level) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendLevelLost(world, level);
                Log.d("godot", "AnalitycsFF: Nivel perdido registrado.");
            }
            });
    }

    /**
     * Metodo invocado para registrar el evento de vidas ganadas (Method to record event of lives won).
     *
     * @param quantity Cantidad de vidas obtenidas. (quantity lives won)
     * @param medium Medio por cual obtuvo las vidas. (Medium that obtained lives)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_live_won(final int quantity, final String medium) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendLiveWon(quantity, medium);
                Log.d("godot", "AnalitycsFF: Vida ganada registrada.");
            }
        });
    }

    /**
     * Metodo invocado para registrar el evento de vidas perdidas (Method to record event of lives lost).
     *
     * @param world Numero del mundo jugado.(Number of world played)
     * @param level Numero del nivel ganado. (Number of level won)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_live_lost(final int world, final int level) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendLiveLost(world, level);
                Log.d("godot", "AnalitycsFF: Vida perdida registrada.");
            }
        });
    }

    /**
     * Metodo invocado para registrar el evento de power ups usado (Method to register the use of a power up).
     *
     * @param world Numero del mundo jugado.(Number of world played)
     * @param level Numero del nivel ganado. (Number of level won)
     * @param item_name Nombre del item. (Name the item)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_pwup_used(final int world, final int level, final String item_name) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendPwupUsed(world, level, item_name);
                Log.d("godot", "AnalitycsFF: Uso de power up utilizado registrado.");
            }
        });
    }

    /**
     * Metodo invocado para registrar el evento de power ups obtenidos (Method to register the obtain of a power up).
     *
     * @param item_name Nombre del item. (Name the item obtained)
     * @param quantity Cantidad de power ups obtenidos. (Quantity power-ups obtained)
     * @param medium Medio por cual obtuvo los power ups. (Medium where he got the power-ups)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_pwup_obtained(final String item_name, final int quantity, final String medium) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendPwupObtained(item_name, quantity, medium);
                Log.d("godot", "AnalitycsFF: Power up obtenido registrado.");
            }
        });
    }

    /**
     * Metodo invocado para registrar el desbloqueo de un mundo (Method to record the world unblocked).
     *
     * @param world Numero del mundo desbloqueado.(Number of world unblocked)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_world_unlocked(final int world) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendWorldUnlocked(world);
                Log.d("godot", "AnalitycsFF: Desbloqueo de mundo registrado.");
            }
        });
    }

    /**
     * Metodo invocado para registrar la seleccion de un nivel (Method to record a level selection to play).
     *
     * @param world Numero del mundo seleccionado.(world number selected)
     * @param level Numero del nivel seleccionado. (Level number selected)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_level_selected(final int world, final int level) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendLevelSelected(world, level);
                Log.d("godot", "AnalitycsFF: Nivel seleccionado registrado.");
            }
        });
    }

    /**
     * Metodo invocado para registrar la seleccion de un mundo (Method to record a world selection).
     *
     * @param world Numero del mundo seleccionado.(world number selected)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_world_selected(final int world) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendWorldSelected(world);
                Log.d("godot", "AnalitycsFF: Mundo seleccionado registrado.");
            }
        });
    }

    /**
     * Metodo invocado para registrar la visualización de un tutorial (Method to record a showed tutorial).
     *
     * @param name_tutorial Nombre del tutorial visualizado.(Name of the showed tutorial)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public void send_tutorial_completed(final String name_tutorial) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                registerDatabase.sendTutorialCompleted(name_tutorial);
                Log.d("godot", "AnalitycsFF: Tutorial completo registrado.");
            }
        });
    }

}
