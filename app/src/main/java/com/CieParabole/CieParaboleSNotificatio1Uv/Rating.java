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
 *
 * Activité qui permet a l'utilisateur de mettre un score de satisfaction de sa session d'aide
 *
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
        //initialisation
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
                scoreSentDialog();
                break;
            case R.id.buttonCANCEL:
                finish();
                break;
            case R.id.buttonNoIntervention:
                Intent intent = new Intent(this,DetectionBeacon.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * Affiche un dialog de confirmation du score
     *
     * */
    private void scoreSentDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Merci !");
        alert.setMessage("Votre score a été envoyé");
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent(Rating.this, DetectionBeacon.class);
                startActivity(intent);
                Rating.this.finish();
            }
        });
        alert.show();
    }
}
