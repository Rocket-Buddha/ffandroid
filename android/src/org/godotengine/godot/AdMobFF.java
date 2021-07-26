/*-----------------------------------------------------------------------*/
/*  AdMobFF.java                                                         */
/*-----------------------------------------------------------------------*/
/*                       This file is part of:                           */
/*                 Android FireBase AdMob Godot Module                   */
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Clase Singleton AdMobFF cuya responsabilidad es ser la Fachada del modulo,
 * y a la vez encapsular toda la logica de FireBase AdMobs. La clase no es publica por que solo
 * es utilizada en el contexto del paquete org.godotengine.godot.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
class AdMobFF extends Godot.SingletonBase {

    // Atributo que referencia la actividad en la que esta corriendo el juego.
    private final Activity activity;
    // Identificador de instancia del objeto al que voy a hacer los callbacks.
    private Integer instanceId;
    // Referencia la banner.
    private AdView adView;
    // Referencia al video reward.
    private RewardedVideoAd rewardedVideoAd;
    // Referencia al interstitial.
    private InterstitialAd interstitialAd;
    // Atributo que define si es una instancia de testing o no.
    private Boolean isTesting;
    //Layout y parametros que se van a usar para setear el banner.
    private FrameLayout layout;
    private FrameLayout.LayoutParams adParams;

    /**
     * Constructor privado de la clase que solo es invocado desde el metodo initialize.
     *
     * @param parameActivity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     */
    private AdMobFF(Activity parameActivity) {
        super.registerClass("AdMobFF", new String[]{
                "set_up",
                "load_banner",
                "load_rewarded_video",
                "load_interstitial",
                "show_banner",
                "show_interstitial",
                "show_rewarded_video",
                "resize_banner",
                "hide_banner",
                "get_banner_width",
                "get_banner_height"
        });
        // Instancio los atributos.
        this.activity = parameActivity;
        this.isTesting = false;
        // Inicializo en un hilo el MobileAds.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(activity);
            }
        });
    }

    /**
     * Metodo invocado una sola vez desde Globals cuando se solicita el Singleton desde GDScript.
     * No debemos preocuparnos de manejar la logica del Singleton ya que esta, esta encapsulada dentro
     * del metodo Globals.get_singleton, verificando un hash map, al ver que no hay value para el objeto, la instancia, una unica vez.
     *
     * @param activity Es la actividad desde la cual se invoca el modulo, o sea, desde la ventana que corre el juego.
     * @return Retorna un Godot.SingletonBase, particularmente el AdMobFF, en este caso.
     */
    static public Godot.SingletonBase initialize(Activity activity) {
        return new AdMobFF(activity);
    }

    /**
     * Metodo invocado para hacer el set_up, este metodo debe ser invocado antes de comenzar a cargar cualquier Ad.
     *
     * @param paramIsTesting  Especifica si esta debe ser una instancia de testing o no.
     * @param paramInstanceId Especifica el identificador de instancia al que haremos los callbacks.
     */
    protected void set_up(final boolean paramIsTesting, final int paramInstanceId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isTesting = paramIsTesting;
                instanceId = paramInstanceId;
                Log.d("godot", "AdMobFF: Setup  basico completado.");
            }
        });
    }

    /**
     * Carga un Banner.
     *
     * @param id      Este es el identificador del banner de AdMob.
     * @param isOnTop Especifica si el banner ira arriba o abajo.
     */
    protected void load_banner(final String id, final boolean isOnTop) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instanceId == null)
                    Log.d("godot", "AdMobFF: No se puede cargar el banner sin primero hacer el setup.");
                else {
                    // Traigo el layout de godot
                    layout = ((Godot) activity).layout;
                    // Preparo los parametros para la vista.
                    adParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
                    if (isOnTop) adParams.gravity = Gravity.TOP;
                    else adParams.gravity = Gravity.BOTTOM;
                    //Preparo la vista cargando sus listeners incluso.
                    adView = new AdView(activity);
                    adView.setAdUnitId(id);
                    adView.setBackgroundColor(Color.TRANSPARENT);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.d("godot", "AdMobFF: onAdLoaded");
                            GodotLib.calldeferred(instanceId, "_on_admob_ad_loaded", new Object[]{});
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            String errorString;
                            switch (errorCode) {
                                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                                    errorString = "ERROR_CODE_INTERNAL_ERROR";
                                    break;
                                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                                    errorString = "ERROR_CODE_INVALID_REQUEST";
                                    break;
                                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                                    errorString = "ERROR_CODE_NETWORK_ERROR";
                                    GodotLib.calldeferred(instanceId, "_on_admob_network_error", new Object[]{});
                                    break;
                                case AdRequest.ERROR_CODE_NO_FILL:
                                    errorString = "ERROR_CODE_NO_FILL";
                                    break;
                                default:
                                    errorString = "" + errorCode;
                                    break;
                            }
                            Log.d("godot", "AdMobFF: Fallo la carga. Codigo de error: " + errorString);
                        }
                    });
                    // Agrego con la vista con los parametros y la vista generada.
                    layout.addView(adView, adParams);
                    // Request
                    AdRequest.Builder adBuilder = new AdRequest.Builder();
                    adBuilder.tagForChildDirectedTreatment(true);
                    if (isTesting) {
                        adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                        adBuilder.addTestDevice(getAdmobDeviceId());
                    }
                    adView.loadAd(adBuilder.build());
                }
            }
        });
    }

    /**
     * Carga un video reward.
     *
     * @param id Identificador de reward en AdMobs.
     */
    protected void load_rewarded_video(final String id) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instanceId == null)
                    Log.d("godot", "AdMobFF: No se puede cargar el reward sin primero hacer el setup.");
                else {
                    // Inicializo el video.
                    rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
                    // Seteo los listeners
                    rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoAdLeftApplication() {
                            Log.d("godot", "AdMobFF: Cambio de aplicacion mientras miraba el video.");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_left_application", new Object[]{});
                        }

                        @Override
                        public void onRewardedVideoAdClosed() {
                            Log.d("godot", "AdMobFF: El usuario cerro el video.");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_closed", new Object[]{});
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int errorCode) {
                            Log.d("godot", "AdMobFF: Fallo la carga del video. Codigo de error: " + errorCode);
                            GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_failed_to_load", new Object[]{errorCode});
                        }

                        @Override
                        public void onRewardedVideoAdLoaded() {
                            Log.d("godot", "AdMobFF: Video cargado.");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_loaded", new Object[]{});
                        }

                        @Override
                        public void onRewardedVideoAdOpened() {
                            Log.d("godot", "AdMobFF: Video abierto.");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_opened", new Object[]{});
                        }

                        @Override
                        public void onRewarded(RewardItem reward) {
                            Log.d("godot", "AdMobFF: " + String.format("Rewarded! Tipo: %s Cantidad: %d", reward.getType(),
                                    reward.getAmount()));
                            GodotLib.calldeferred(instanceId, "_on_rewarded", new Object[]{reward.getType(), reward.getAmount()});
                        }

                        @Override
                        public void onRewardedVideoStarted() {
                            Log.d("godot", "AdMobFF: Video iniciado.");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_video_started", new Object[]{});
                        }
                    });
                    rewardedVideoAd.loadAd(id, new AdRequest.Builder().build());
                }
            }
        });
    }

    /**
     * Cargo un interstitial.
     *
     * @param id Identificador de interstitial de AdMob.
     */
    protected void load_interstitial(final String id) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instanceId == null)
                    Log.d("godot", "AdMobFF: No se puede cargar el interstitial sin primero hacer el setup.");
                else {
                    interstitialAd = new InterstitialAd(activity);
                    interstitialAd.setAdUnitId(id);
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.d("godot", "AdMobFF: Interstitial cargado.");
                            GodotLib.calldeferred(instanceId, "_on_interstitial_loaded", new Object[]{});
                        }

                        @Override
                        public void onAdClosed() {
                            GodotLib.calldeferred(instanceId, "_on_interstitial_closed", new Object[]{});
                            AdRequest.Builder adBuilder = new AdRequest.Builder();
                            adBuilder.tagForChildDirectedTreatment(true);
                            if (isTesting) {
                                adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                                adBuilder.addTestDevice(getAdmobDeviceId());
                            }
                            interstitialAd.loadAd(adBuilder.build());
                            Log.d("godot", "AdMobFF: Interstitial cerrado.");
                        }
                    });

                    AdRequest.Builder adBuilder = new AdRequest.Builder();
                    adBuilder.tagForChildDirectedTreatment(true);
                    if (isTesting) {
                        adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                        adBuilder.addTestDevice(getAdmobDeviceId());
                    }
                    interstitialAd.loadAd(adBuilder.build());
                }
            }
        });
    }

    /**
     * Muestra el banner.
     *
     */
    protected void show_banner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adView.getVisibility() == View.VISIBLE) return;
                adView.setVisibility(View.VISIBLE);
                adView.resume();
                Log.d("godot", "AdMobFF: Banner Mostrado.");
            }
        });
    }

    /**
     * Muestro el video reward.
     *
     */
    protected void show_rewarded_video() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rewardedVideoAd.isLoaded()) {
                    rewardedVideoAd.show();
                }
            }
        });
    }

    /**
     * Muestra el interstitial.
     *
     */
    protected void show_interstitial() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Log.w("godot", "AdMobFF: El interstitial no esta cargado.");
                    GodotLib.calldeferred(instanceId, "_on_interstitial_not_loaded", new Object[]{});
                }
            }
        });
    }

    /**
     * Re-configura el layout del banner.
     *
     */
    protected void resize_banner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeView(adView); // Remove the old view
                // Extrae los parametros seteados.
                int gravity = adParams.gravity;
                FrameLayout layout = ((Godot) activity).layout;
                adParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                adParams.gravity = gravity;
                AdListener adListener = adView.getAdListener();
                String id = adView.getAdUnitId();
                // Create la nueva vista con los parametros viejos.
                adView = new AdView(activity);
                adView.setAdUnitId(id);
                adView.setBackgroundColor(Color.TRANSPARENT);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdListener(adListener);
                // Agrega la vista y los parametros al layout.
                layout.addView(adView, adParams);
                // Ejecuta el request.
                AdRequest.Builder adBuilder = new AdRequest.Builder();
                adBuilder.tagForChildDirectedTreatment(true);
                if (isTesting) {
                    adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                    adBuilder.addTestDevice(getAdmobDeviceId());
                }
                // Carga un nuevo banner.
                adView.loadAd(adBuilder.build());
                Log.d("godot", "AdMobFF: Banner re-configurado");
            }
        });
    }

    /**
     * Oculta el banner.
     *
     */
    protected void hide_banner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Si no esta oculto, lo oculto.
                if (adView.getVisibility() != View.GONE) {
                    adView.setVisibility(View.GONE);
                    adView.pause();
                    Log.d("godot", "AdMobFF: Banner ocultado.");
                }
            }
        });
    }

    /**
     * Obtener el ancho del banner.
     *
     */
    protected int get_banner_width() {

        return AdSize.SMART_BANNER.getWidthInPixels(activity);
    }

    /**
     * Obtener el alto del banner.
     *
     */
    protected int get_banner_height() {

        return AdSize.SMART_BANNER.getHeightInPixels(activity);
    }

    /**
     * Obtener el identificador del dispositivo (hardware), hasheado con MD5 como lo usa GOOGLE.
     * Solo para testing, no es una best practice utilizar el identificador para nada.
     * https://developers.google.com/admob/android/test-ads
     *
     * @return El identificador de hardware hasheado en MD5.
     */
    @SuppressLint("HardwareIds")
    private String getAdmobDeviceId() {
        return md5(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID))
                .toUpperCase(Locale.US);
    }

    /**
     * Metodo helper para hashear un String con MD5.
     *
     * @return El string hasheado en MD5.
     */
    private String md5(final String s) {
        try {
            // Crea el hash.
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Crea el HEX.
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.d("godot", "AdMobFF: Error en Hashing MD5: " + e.getMessage());
        }
        return "";
    }
}