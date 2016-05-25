package com.example.quentin.mobilautonomie;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.oralb.sdk.OBTSDK;

/**
 * Created by Althian on 24/05/2016.
 */
public class ToothBrushService extends Service{

    private PowerManager.WakeLock wakelock;

    private DemoApplication api;

    public ToothBrushService(){
        super();
    }

    public void onCreate(){

        super.onCreate();

        //set the WakeLock
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //We get the user ID



        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class DemoApplication extends Application {


        @Override
        public void onCreate() {
            super.onCreate();
            try {
                //Call to initialize the OBTSDK
                OBTSDK.initialize(this);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}


