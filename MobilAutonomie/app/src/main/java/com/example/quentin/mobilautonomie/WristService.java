package com.example.quentin.mobilautonomie;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.net.URL;

/**
 * Created by Althian on 21/05/2016.
 */
public class WristService extends Service {

    private URL serverUrl;
    private PowerManager.WakeLock wakelock;

    private String oAuth;
    private String endUser;
    private String accessToken;

    public WristService(){
        super();
    }

    public void onCreate(){
        super.onCreate();

        //set the WakeLock
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

    }

    public void onStart(){

        //set the WakeLock
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        //We check if the smartphone is connected via wifi.

        ConnectivityManager comManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netw = comManager.getActiveNetworkInfo();

        if(netw != null && netw.getType() == ConnectivityManager.TYPE_WIFI){

        }

    }

    public void onDestroy(){

        super.onDestroy();
        wakelock.release();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
