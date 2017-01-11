package com.CieParabole.CieParaboleSNotificatio1Uv;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;


public class DetectionBeacon extends AppCompatActivity implements BeaconConsumer,View.OnClickListener{

    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;
    private int notificationID = 0;
    private String id;
    private String tokenSession = null;
    WebService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        WebService service = new WebService();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);

        if(tokenSession != null){
            setTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonOUI:
                callWebService();
                break;
            case R.id.buttonNON:
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeaons",null, null, null);
        setContentView(R.layout.activty_detection_find);
        ImageButton buttonOui = (ImageButton) findViewById(R.id.buttonOUI);
        ImageButton buttonNon = (ImageButton) findViewById(R.id.buttonNON);
        buttonOui.setOnClickListener(this);
        buttonNon.setOnClickListener(this);
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    notificationID = 0;
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                double m=0;
                if (notificationID == 0) {
                    addNotification();
                }
                for (Beacon oneBeacon : beacons) {
                    m = oneBeacon.getDistance();
                    if(m<oneBeacon.getDistance()){
                        id = oneBeacon.getId1().toString() + oneBeacon.getId2().toString() + oneBeacon.getId3().toString();
                    }
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());


                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



    }
    private void addNotification() {
        Intent resultIntent = new Intent(this, DetectionBeacon.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("WeGuide")
                .setContentText("Beacon détecté")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, builder.build());


    }

    private void callWebService(){
        ArrayList<String> list = service.getAllBeacons();
        if(checkbeacon(list)){
            tokenSession = service.requestHelp(this,id);
            setTimer();
        }
    }

    public boolean checkbeacon(ArrayList<String> list){

        for(int i=0; i<list.size(); i++){
            if(list.get(i).equals(id)){
                return true;
            }
        }
        return false;

    }

    public void setTimer(){
        TokenCheckTimer timerTask = new TokenCheckTimer(service,tokenSession);
        //running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
