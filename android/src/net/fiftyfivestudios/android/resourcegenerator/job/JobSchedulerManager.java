/*-----------------------------------------------------------------------*/
/*   JobSchedulerManager.java                                            */
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

package net.fiftyfivestudios.android.resourcegenerator.job;

import android.content.Context;
import android.os.Bundle;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Esdasd
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
public class JobSchedulerManager {

    //
    private static JobSchedulerManager ourInstance;
    // Atributo de referencia al dispatcher, necesario para planificar un Job Service.
    private final FirebaseJobDispatcher dispatcher;

    private JobSchedulerManager(Context context) {
        // Declaro e instancio un dispatcher.
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    public synchronized static JobSchedulerManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new JobSchedulerManager(context);
            return ourInstance;
        } else return ourInstance;
    }


    public void schedule_job(String jobUId, String notifSubject,
                             String notifMessage,
                             int freq,
                             int freqWin,
                             Class<ResourceGeneratorJob> cls) {


        Bundle bundle = createBundle(notifSubject, notifMessage);
        // Buildeo el Job, planifico y retorno.
        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(cls) // El servicio que va a ser llamado cuando se cumpla el plazo.
                        .setTrigger(Trigger.executionWindow(freq, freq + freqWin)) // Ventana de tiempo.
                        .setLifetime(Lifetime.FOREVER) // El servicio continuara funcionando despues de reboot incluso.
                        .setReplaceCurrent(false) // Si existe un Job con mismo Id no lo pisa.
                        .setTag(jobUId) // Setea el Identificador de Job.
                        //.setRetryStrategy(RetryStrategy.DEFAULT_LINEAR) // El Job se ejecutara cada t, linealmente, siendo t el tiempo definido.
                        .setExtras(bundle) // Setea el bundle con el mensaje.
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .build()
        );
    }

    /**
     * Metodo invocado para cancelar un Job determinado.
     *
     * @param jobUId El identificador unico del Job.
     */
    public void kill_job(final String jobUId) {
        dispatcher.cancel(jobUId);
    }

    private Bundle createBundle(String notifTitle,
                                String notifMessage) {

        // Declaro e instancio un bundle para mandar el mensaje.
        // Eventualmente aca se podrian setear mas datos para modificar el comportamiento en el callback.
        final Bundle bundle = new Bundle();
        bundle.putString("notifTitle", notifTitle);
        bundle.putString("notifMessage", notifMessage);
        return bundle;
    }


}
