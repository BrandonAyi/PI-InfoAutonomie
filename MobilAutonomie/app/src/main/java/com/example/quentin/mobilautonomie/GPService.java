package com.example.quentin.mobilautonomie;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Quentin on 18/05/2016.
 */
public class GPService extends Service implements LocationListener{

    private LocationManager gps;
    private String provider;
    private Location location;
    private PowerManager.WakeLock wakelock;
    private URL serverUrl;


    public GPService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //set the WakeLock
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

    }

    private void locationStream(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Get the location manager
        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(gps.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent askUser = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(askUser);
        }

        //Determine how to select the provider of the location with the default parameters
        Criteria crit = new Criteria();
        provider = gps.getBestProvider(crit,false);

        int permission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission < 0)
        {
            Location location = gps.getLastKnownLocation(provider);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        wakelock.release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        this.location.set(location);

        if(this.isConnected(getApplicationContext())){

            StringBuilder sb = new StringBuilder();
            HttpURLConnection co = null;

            try {

                co = (HttpURLConnection) serverUrl.openConnection();
                co.setDoOutput(true);
                co.setRequestMethod("POST");
                co.setUseCaches(false);
                co.setConnectTimeout(10000);
                co.setReadTimeout(10000);
                co.setRequestProperty("Content-Type","application/json");

                co.setRequestProperty("Host", "android.schoolportal.gr");
                co.connect();

                //Create JSONObject here
                JSONObject loc = new JSONObject();
                loc.put("longitude", location.getLongitude());
                loc.put("latitude", location.getLatitude());

                OutputStreamWriter out = new   OutputStreamWriter(co.getOutputStream());
                out.write(loc.toString());
                out.close();

                int HttpResult =co.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            co.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    System.out.println(""+sb.toString());

                }else{
                    System.out.println(co.getResponseMessage());
                }
            } catch (MalformedURLException e) {

                e.printStackTrace();
            }
            catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                if(co!=null)
                    co.disconnect();
            }
            SystemClock.sleep(60000);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //This function is used for testing if the smartphone is connected to Internet, it is used to test if we can relate the datas.
    //There is a "if" checking the phone version to be sure to use the appropriate methods.

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {

                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}


