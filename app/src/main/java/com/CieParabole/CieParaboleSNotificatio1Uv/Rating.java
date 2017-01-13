package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

/**
 * Created by jadhaddad on 1/11/17.
 */

public class Rating extends Activity implements View.OnClickListener{
    private RatingBar ratingBar;
    private Button buttonOK;
    private Button buttonCancel;
    private Button buttonNoIntervention;
    private WebService webService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        buttonOK = (Button)findViewById(R.id.buttonOK);
        buttonCancel = (Button) findViewById(R.id.buttonCANCEL);
        buttonNoIntervention = (Button) findViewById(R.id.buttonNoIntervention);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        buttonOK.setOnClickListener(this);
        buttonNoIntervention.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        webService = new WebService();
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonOK:
                int rating = (int)ratingBar.getRating();
                webService.sendRating(token, rating);
                finishAppDialog();
                break;
            case R.id.buttonCANCEL:
                finishAppDialog();
                finish();
                break;
            case R.id.buttonNoIntervention:
                Intent intent = new Intent(this,DetectionBeacon.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void finishAppDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("");
        alert.setMessage("Please fill in all the fields to proceed");
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.show();
    }
}
