package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Aline on 11/01/2017.
 *
 * Classe qui verifie que la session est toujours en cours
 *
 */
public class TokenCheckTimer extends TimerTask {

    int i=0;
    private WebService service;
    private String token;
    private Context context;
    private Timer timer;

    TokenCheckTimer(WebService s, String t, Context c, Timer timer){
        service = s;
        token = t;
        context = c;
        this.timer = timer;
    }

    @Override
    public void run() {
        System.out.println("Timer task started at:");
        completeTask();
        System.out.println("Timer task finished at:");
    }

    private void completeTask() {
        Log.d("ji", "completeTask:jkj ");
        if( !service.checkToken(token)){

//            if(i==0){
//                service.newPosition(token, id);
//                i++;
//            }
            Log.d("ji", ""+service.checkToken(token));
            timer.cancel();
            Intent intent = new Intent(context,Rating.class);
            intent.putExtra("token",token);
            context.startActivity(intent);

        }
    }
}
