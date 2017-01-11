package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Aline on 11/01/2017.
 */
public class ServiceDetection extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
