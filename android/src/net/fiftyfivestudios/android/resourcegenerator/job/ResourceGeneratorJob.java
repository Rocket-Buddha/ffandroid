/*-----------------------------------------------------------------------*/
/*   ResourceGeneratorJob.java                                            */
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


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.godot.game.R;

/**
 * Esta clase define el comportamiento del servicio que sera ejecutado para regernerar recursos y notificaciones.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
 public class ResourceGeneratorJob extends JobService {


    /**
     * Metodo invocado cuando el se inicia el Job.
     *
     * @param job El Job en si mismo, con el bundle que fue cargado cuando se creo.
     * @return Si necesita ser replanificado.
     */
    @Override
    public boolean onStartJob(JobParameters job) {

        Log.d("godot", "arranco el job");
        /**SharedPreferences preferences = this
                .getSharedPreferences("activity", this.MODE_PRIVATE);*/

        //if(preferences.getBoolean("run-notif", false)){
            Log.d("godot", "no activa entro");
            sendNotification(job.getExtras());
        //}

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters var1){

        return true;
    }

    protected void sendNotification(Bundle bundle) {

        Log.d("godot", "RGFF: Creando notificacion");
        try {
            //
            Intent intent = new Intent(this, org.godotengine.godot.Godot.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 7002, intent, PendingIntent.FLAG_ONE_SHOT);
            //
            Uri defaultSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_skull)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_hero))
                    .setContentTitle(bundle.getString("notifTitle"))
                    .setContentText(bundle.getString("notifMessage"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            //
            ((NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(7002, nBuilder.build());

        } catch (Exception e) {
            Log.d("godot", "RGFF: Error en la creacion de la notificacion: " + e.getMessage());
        }
    }

}
