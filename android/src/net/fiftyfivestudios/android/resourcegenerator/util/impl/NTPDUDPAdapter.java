/*-----------------------------------------------------------------------*/
/*    NTPDUDPAdapter.java                                                */
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

package net.fiftyfivestudios.android.resourcegenerator.util.impl;

import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import net.fiftyfivestudios.android.resourcegenerator.util.ITimeClient;

import java.io.IOException;
import java.net.InetAddress;


/**
 * Clase que implementa el NTPUDPClient para la obtencion de tiempo desde los servidores de Google.
 *
 * @author Andres Lusi <andres.julian.lusi@gmail.com>
 * @version 1.0
 */
public class NTPDUDPAdapter implements ITimeClient {

    // Constante de Array de Strings que guarda el pool de servidores.
    private static final String[] hosts = new String[]{
            "time4.google.com",
            "time3.google.com",
            "time2.google.com",
            "time1.google.com",
    };
    // Contante que define el maximo timeout posible
    private static final Integer DEFAULT_TIMEOUT = 5000;
    // Atributo instance del Singleton.
    private static NTPDUDPAdapter ourInstance;
    // Cliente NTPUDP
    private NTPUDPClient client;

    /**
     * Constructor privado.
     */
    private NTPDUDPAdapter() {
        client = new NTPUDPClient();
        client.setDefaultTimeout(DEFAULT_TIMEOUT);
    }

    /**
     * Metodo que se invoca para obtener el Singleton.
     *
     * @return Unico NTPDUDPAdapter. Singleton.
     */
    public static synchronized NTPDUDPAdapter getInstance() {
        if (ourInstance == null) {
            ourInstance = new NTPDUDPAdapter();
            return ourInstance;
        } else return ourInstance;
    }

    /**
     * Metoddo invocado para obtener el tiempo de los servidores de Google.
     *
     * @return Date con el tiempo actual en UTC.
     */
    public Integer getTimeFromInternet() {
        for (String host : hosts) {
            try {
                // Obtengo la direccion IP.
                InetAddress hostAddr = InetAddress.getByName(host);
                // Obtengo la informacion del protocolo y la wrappeo en un objeto.
                TimeInfo info = client.getTime(hostAddr);
                // Retorno el tiempo el Long.
                return (int)(info.getReturnTime()/1000);
            } catch (IOException e) {
                Log.d("godot", "RGFF: Error en el cliente NTPUDP: " + e.toString());
            } finally {
                client.close();
            }
        }
        return null;
    }
}