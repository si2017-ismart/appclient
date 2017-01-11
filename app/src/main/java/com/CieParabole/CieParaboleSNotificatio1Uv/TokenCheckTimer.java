package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.content.Intent;
import android.util.Log;

import java.util.TimerTask;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Aline on 11/01/2017.
 */
public class TokenCheckTimer extends TimerTask {

    private WebService service;
    private String token;

    TokenCheckTimer(WebService s, String t){
        service = s;
        token = t;
    }

    @Override
    public void run() {
        System.out.println("Timer task started at:");
        completeTask();
        System.out.println("Timer task finished at:");
    }

    private void completeTask() {
        try {
            Log.d("ji", "completeTask:jkj ");
            /*if( !service.checkToken()){
                this.cancel();
            }*/
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
