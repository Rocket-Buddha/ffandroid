/*-----------------------------------------------------------------------*/
/*   ResourceGeneratorFF.java                                            */
/*-----------------------------------------------------------------------*/
/*                       This file is part of:                           */
/*        Android FireBase Notification Dispatcher Godot Module          */
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
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import net.fiftyfivestudios.android.resourcegenerator.job.JobSchedulerManager;
import net.fiftyfivestudios.android.resourcegenerator.job.ResourceGeneratorJob;
import net.fiftyfivestudios.android.resourcegenerator.util.impl.NTPDUDPAdapter;


/**
 * Clase Singleton  ResourceGeneratorFF cuya responsabilidad es ser la Fachada del modulo,
 * y a la vez encapsular toda la logica del modulo de regeneracion de recursos. La clase no es publica por que solo
 * es utilizada en el contexto del paquete org.godotengine.godot.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
class ResourceGeneratorFF extends Godot.SingletonBase {

    // Atributo que referencia la actividad en la que esta corriendo el juego.
    private final Activity activity;
    // Atributo que referencia la intancia a la que debe hacer los callbacks.
    private Integer instanceId;
    private String pauseCallback;
    private String resumeCallback;


    /**
     * Constructor privado de la clase que solo es invocado desde el metodo initialize.
     *
     * @param paramActivity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     */
    private ResourceGeneratorFF(Activity paramActivity) {
        // Registro los metodos que voy a consumir desde godot.
        registerClass("ResourceGeneratorFF", new String[]{
                "set_up",
                "schedule_notification",
                "get_timestamp",
                "kill_job",
                "clean_notifications",
        });
        // Instancio los atributos.
        activity = paramActivity;
    }

    /**
     * Metodo invocado una sola vez desde Globals cuando se solicita el Singleton desde GDScript.
     * No debemos preocuparnos de manejar la logica del Singleton ya que esta, esta encapsulada dentro
     * del metodo Globals.get_singleton, verificando un hash map, al ver que no hay value para el objeto, la instancia, una unica vez.
     *
     * @param activity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @return Retorna un Godot.SingletonBase, particularmente el FireNotFF, en este caso.
     */
    static protected Godot.SingletonBase initialize(Activity activity) {
        return new ResourceGeneratorFF(activity);
    }


    /**
     * Metodo invocado para hacer el setup del modulo.
     *
     * @param instanceId
     * @param pauseCallback
     * @param resumeCallback
     */
    protected void set_up(final int instanceId, final String pauseCallback,
                          final String resumeCallback) {
        this.instanceId = instanceId;
        this.pauseCallback = pauseCallback;
        this.resumeCallback = resumeCallback;

        Log.d("godot", "RGFF: Modulo cargado y configurado.");
    }

    protected void schedule_notification(final String jobUId, final String notifSubject,
                                         final String notifMessage,
                                         final int freq,
                                         final int freqWin) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                JobSchedulerManager.getInstance(activity).schedule_job(jobUId, notifSubject,
                        notifMessage,
                        freq,
                        freqWin,
                        ResourceGeneratorJob.class);
            }
        });
    }

    /*protected void get_timestamp(final int instanceCallback, final String callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("godot", "entrando a get timestamp");
                Integer timestamp = NTPDUDPAdapter.getInstance().getTimeFromInternet();

                GodotLib.calldeferred(instanceCallback, callback, new Object[]{timestamp});
                Log.d("godot", "RGFF: " + String.valueOf(timestamp));
            }
        });
    }*/


    @Override
    protected void onMainResume() {
        //setActiveFlag(false);
        if (instanceId != null)
            GodotLib.calldeferred(instanceId, resumeCallback, new Object[]{});
    }

    @Override
    protected void onMainPause() {
        //setActiveFlag(true);
        if (instanceId != null)
            GodotLib.callobject( instanceId, pauseCallback, new Object[]{});
            //GodotLib.calldeferred(instanceId, pauseCallback, new Object[]{});
    }


    /**protected void setActiveFlag(Boolean flag) {
        SharedPreferences sp = activity.getSharedPreferences("activity", activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("run-notif", flag);
        ed.commit();
    }*/

    /**
     * Metodo invocado para cancelar un Job determinado.
     *
     * @param jobUId El identificador unico del Job.
     */
    protected void kill_job(final String jobUId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instanceId != null) {
                    JobSchedulerManager.getInstance(activity)
                            .kill_job(jobUId);
                } else {
                    Log.d("godot", "RGFF: Debes hacer primero el setup del modulo.");
                }
            }
        });
    }

    /**
     * Metodo invocado para limpiar todas las notificaciones vinculadas a la aplicacion principal.
     */
    protected void clean_notifications() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((NotificationManager)
                        activity.getSystemService(Context.NOTIFICATION_SERVICE))
                        .cancelAll();
            }
        });
    }


}