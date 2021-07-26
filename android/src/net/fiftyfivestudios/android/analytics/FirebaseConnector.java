package net.fiftyfivestudios.android.analytics;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.fiftyfivestudios.android.spi.AnalyticsConnector;


public class FirebaseConnector implements AnalyticsConnector {

    private FirebaseAnalytics firebaseAnalytics;
    private static FirebaseConnector ourInstance;

    /**
     * Constructor privado de la clase (Class private constructor).
     *
     * @param parameActivity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    private FirebaseConnector(Activity parameActivity){
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(parameActivity);
    }

    /**
     * Metodo a consultar para obtener la instancia de la clase, es un singleton (Method that your a consulting for obtained a instance class).
     *
     * @param activity Actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    public static synchronized AnalyticsConnector getInstance(Activity activity) {
        if (ourInstance == null) {
            ourInstance = new FirebaseConnector(activity);
            return ourInstance;
        }
        return ourInstance;
    }

    /**
     * Metodo invocado para registrar un evento cualquiera por su nombre (Method to record an event with any name that you need).
     *
     * @param eventName Nombre del evento sin espacios, se puede usar guion bajo. (Name event to record, you do not use space but if you can use hyphen-medium")
     * @author Andres Lusi <andres.julian.lusi@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendEvent(final String eventName) {
        firebaseAnalytics.logEvent(eventName, new Bundle());
    }

    /**
     * Metodo invocado para registrar el evento el nivel ganado (Method to record event of won level).
     *
     * @param world Numero del mundo jugado en formato texto.(Number of world played)
     * @param level Numero del nivel ganado en formato texto. (String of level won)
     * @param stars Cantidad de estrellas obtenidas en el nivel ganado en formato texto. (Quantity of star obtained)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendLevelWon(final String world, final String level, final String stars) {
        Bundle bundle = new Bundle();
        bundle.putString("world", world);
        bundle.putString(FirebaseAnalytics.Param.LEVEL, level);
        bundle.putString("stars", stars);
        firebaseAnalytics.logEvent("level_won", bundle);
    }

    /**
     * Metodo invocado para registrar el evento de nivel perdido (Method to record event of lost level).
     *
     * @param world Numero del mundo jugado.(Number of world played)
     * @param level Numero del nivel ganado. (Number of level won)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendLevelLost(final Integer world, final Integer level) {
        Bundle bundle = new Bundle();
        bundle.putInt("world", world);
        bundle.putInt(FirebaseAnalytics.Param.LEVEL, level);
        firebaseAnalytics.logEvent("level_lost", bundle);
    }

    /**
     * Metodo invocado para registrar el evento de vidas ganadas (Method to record event of lives won).
     *
     * @param quantity Cantidad de vidas obtenidas. (quantity lives won)
     * @param medium Medio por cual obtuvo las vidas. (Medium that obtained lives)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendLiveWon(final Integer quantity, final String medium) {
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.QUANTITY, quantity);
        bundle.putString(FirebaseAnalytics.Param.MEDIUM , medium);
        firebaseAnalytics.logEvent("live_won", bundle);
    }

    /**
     * Metodo invocado para registrar el evento de vidas perdidas (Method to record event of lives lost).
     *
     * @param world Numero del mundo jugado.(Number of world played)
     * @param level Numero del nivel ganado. (Number of level won)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendLiveLost(final Integer world, final Integer level) {
        Bundle bundle = new Bundle();
        bundle.putInt("world", world);
        bundle.putInt(FirebaseAnalytics.Param.LEVEL, level);
        firebaseAnalytics.logEvent("live_lost", bundle);
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
    @Override
    public void sendPwupUsed(final Integer world, final Integer level, final String item_name) {
        Bundle bundle = new Bundle();
        bundle.putInt("world" , world);
        bundle.putInt(FirebaseAnalytics.Param.LEVEL, level);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item_name);
        firebaseAnalytics.logEvent("pwup_used", bundle);
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
    @Override
    public void sendPwupObtained(final String item_name, final Integer quantity, final String medium) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item_name);
        bundle.putInt(FirebaseAnalytics.Param.QUANTITY, quantity);
        bundle.putString(FirebaseAnalytics.Param.MEDIUM , medium);
        firebaseAnalytics.logEvent("pwup_obtained", bundle);
    }

    /**
     * Metodo invocado para registrar el desbloqueo de un mundo (Method to record the world unblocked).
     *
     * @param world Numero del mundo desbloqueado.(Number of world unblocked)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendWorldUnlocked(final Integer world) {
        Bundle bundle = new Bundle();
        bundle.putInt("world", world);
        firebaseAnalytics.logEvent("world_unlocked", bundle);
    }

    /**
     * Metodo invocado para registrar la seleccion de un nivel (Method to record a level selection to play).
     *
     * @param world Numero del mundo seleccionado.(world number selected)
     * @param level Numero del nivel seleccionado. (Level number selected)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendLevelSelected(final Integer world, final Integer level) {
        Bundle bundle = new Bundle();
        bundle.putInt("world", world);
        bundle.putInt(FirebaseAnalytics.Param.LEVEL, level);
        firebaseAnalytics.logEvent("level_selected", bundle);
    }

    /**
     * Metodo invocado para registrar la seleccion de un mundo (Method to record a world selection).
     *
     * @param world Numero del mundo seleccionado.(world number selected)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendWorldSelected(final Integer world) {
        Bundle bundle = new Bundle();
        bundle.putInt("world", world);
        firebaseAnalytics.logEvent("world_selected", bundle);
    }

    /**
     * Metodo invocado para registrar la visualización de un tutorial (Method to record a showed tutorial).
     *
     * @param name_tutorial Nombre del tutorial visualizado.(Name of the showed tutorial)
     * @author Eduardo David Angeleri <Angeleridavid@gmail.com>
     * @version 1.0
     */
    @Override
    public void sendTutorialCompleted(final String name_tutorial) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, name_tutorial);
        firebaseAnalytics.logEvent("tutorial_completed", bundle);
    }

}