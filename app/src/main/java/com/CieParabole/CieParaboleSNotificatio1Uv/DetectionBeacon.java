package com.CieParabole.CieParaboleSNotificatio1Uv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.CieParabole.CieParaboleSNotificatio1Uv.MyApplication;
import com.CieParabole.CieParaboleSNotificatio1Uv.R;
import com.CieParabole.CieParaboleSNotificatio1Uv.estimote.BeaconID;
import com.CieParabole.CieParaboleSNotificatio1Uv.estimote.EstimoteCloudBeaconDetails;
import com.CieParabole.CieParaboleSNotificatio1Uv.estimote.EstimoteCloudBeaconDetailsFactory;
import com.CieParabole.CieParaboleSNotificatio1Uv.estimote.ProximityContentManager;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.cloud.model.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DetectionBeacon extends AppCompatActivity{



    private static final String TAG = "MainActivity";

    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();

    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(188, 15, 92);

    private ProximityContentManager proximityContentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        proximityContentManager = new ProximityContentManager(this,
                Arrays.asList(
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 44321, 38148),
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 121, 6227),
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 49647, 21143)),
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                String text;
                if (content != null) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    text = "You're in " + beaconDetails.getBeaconName() + "'s range!";
                } else {
                    text = "No beacons in range.";
                }
                ((TextView) findViewById(R.id.textView)).setText(text);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ProximityContentManager content updates");
        proximityContentManager.stopContentUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }

}
