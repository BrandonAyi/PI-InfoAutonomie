package com.example.quentin.mobilautonomie;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{

    private Intent gps, wristband, toothbrush;
    private Button quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quit = (Button) findViewById(R.id.Quit);

        quit.setOnTouchListener(this);

        //Geolocation
        gps = new Intent();
        gps.setAction("com.example.quentin.mobilautonomie.GPService");
        startService(gps);

        //Wristband
        wristband = new Intent();
        wristband.setAction("com.example.quentin.mobilautonomie.WristService");
        startService(gps);

        //Toothbrush
        toothbrush = new Intent();
        toothbrush.setAction("com.example.quentin.mobilautonomie.ToothBrushService");
        startService(gps);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return true;
    }

    @Override
    public void onClick(View v) {

        MainActivity.this.stopService(gps);
        MainActivity.this.stopService(wristband);
        MainActivity.this.stopService(toothbrush);

    }
}
