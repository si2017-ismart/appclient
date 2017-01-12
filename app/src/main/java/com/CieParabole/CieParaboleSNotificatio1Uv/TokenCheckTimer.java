package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.TimerTask;


/**
 * Created by Aline on 11/01/2017.
 */
public class TokenCheckTimer extends TimerTask {

    private WebService service;
    private String token;
    private Context context;

    TokenCheckTimer(WebService s, String t, Context c){
        service = s;
        token = t;
        context = c;
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
            if( !service.checkToken(token)){
                this.cancel();
                Intent intent = new Intent(context,Rating.class);
                context.startActivity(intent);

            }
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
