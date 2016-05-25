package com.example.quentin.mobilautonomie;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.oralb.sdk.OBTBrush;
import com.oralb.sdk.OBTBrushListener;
import com.oralb.sdk.OBTBrushListenerAdapter;
import com.oralb.sdk.OBTSDK;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Althian on 24/05/2016.
 */
public class ToothBrushService extends Service implements OBTBrushListener{

    private PowerManager.WakeLock wakelock;
    private List<OBTBrush> brushList;
    private OBTBrushListenerAdapter obtBrushAdapter;

    public ToothBrushService() {
        super();
    }

    public void onCreate() {

        super.onCreate();

        //set the WakeLock
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        try {
            //Call to initialize the OBTSDK
            OBTSDK.initialize(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //We get the user ID

        setContentView(R.layout.activity_bluetooth);

        // Creating a ListView to show nearby Oral-B Toothbrushes
        ListView nearbyOralBToothbrushList = (ListView) findViewById(R.id.found_brushes_list);

        // Instantiate a custom adapter to hold the Oral-B Toothbrushes and set it to the list
        obtBrushAdapter = new OBTBrushAdapter();
        nearbyOralBToothbrushList.setAdapter(obtBrushAdapter);

        // Set OnItemClickListener to the ListView
        nearbyOralBToothbrushList.setOnItemClickListener(new AdapterView.OnItemClickListener() {



            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Connect to chosen Oral-B Toothbrush
                OBTSDK.connectToothbrush(obtBrushAdapter.getItem(position));
            }
        });

    }

        return super.onStartCommand(intent, flags, startId);
    }
    public void onDestroy(){

        super.onDestroy();
        wakelock.release();

    }

    @Override
    public void onNearbyBrushesFoundOrUpdated(List<OBTBrush> list) {

        // Clear the custom adapter and set found Oral-B Toothbrushes as items
        OBTSDK.setOBTBrushListener(this);

    }


    @Override
    public void onBrushConnected() {

    }

    @Override
    public void onHighPressureChanged(boolean b) {

    }

    @Override
    public void onSectorChanged(int i) {

    }

    @Override
    public void onBatteryLevelChanged(float v) {

    }

    @Override
    public void onBrushingTimeChanged(long l) {

    }

    @Override
    public void onBrushStateChanged(int i) {

    }

    @Override
    public void onBluetoothError() {

    }

    @Override
    public void onBrushDisconnected() {

    }

    @Override
    public void onRSSIChanged(int i) {

    }

    @Override
    public void onBrushingModeChanged(int i) {

    }

    @Override
    public void onBrushConnecting() {

    }


}


