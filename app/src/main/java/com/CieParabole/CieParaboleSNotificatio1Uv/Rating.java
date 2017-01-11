package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.app.Activity;
import android.os.Bundle;
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonOK:
                float rating = ratingBar.getRating();
                //send rating
                break;
            case R.id.buttonCANCEL:
                finish();
                break;
            case R.id.buttonNoIntervention:
                break;
        }
    }
}
