package com.philips.lighting.quickstart;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class MyApplicationActivity extends Activity {
    // instantiate variables
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    DiscoTask discoTask = new DiscoTask(); // async task


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();
        final PHBridge bridge = phHueSDK.getSelectedBridge();
        final List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        // buttons
        Button redButton;
        redButton = (Button) findViewById(R.id.buttonRed);
        redButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                redLights(allLights, bridge);
            }

        });
        Button greenButton;
        greenButton = (Button) findViewById(R.id.buttonGreen);
        greenButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                greenLights(allLights, bridge);
            }

        });
        Button offButton;
        offButton = (Button) findViewById(R.id.buttonOff);
        offButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offLights(allLights, bridge);
            }

        });
        Button discoButton;
        discoButton = (Button) findViewById(R.id.buttonDisco);
        discoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                discoLights(allLights, bridge);
            }

        });
        Button blinkButton;
        blinkButton = (Button) findViewById(R.id.buttonBlink);
        blinkButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                blinkLights(allLights, bridge);
            }

        });

    }



    public void greenLights(List<PHLight> allLights, PHBridge bridge) {
            for (PHLight light : allLights) {
                PHLightState lightState = new PHLightState();
                lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                lightState.setOn(true);
                lightState.setTransitionTime(1);
                lightState.setBrightness(225);
                lightState.setX((float)0.409);
                lightState.setY((float)0.518);
                bridge.updateLightState(light, lightState);
            }
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("http://redgreenlight.herokuapp.com/setstate/0");
        }

    public void redLights(List<PHLight> allLights, PHBridge bridge) {
            for (PHLight light : allLights) {
                PHLightState lightState = new PHLightState();
                lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                lightState.setOn(true);
                lightState.setTransitionTime(1);
                lightState.setBrightness(225);
                lightState.setX((float)0.675);
                lightState.setY((float)0.322);
                bridge.updateLightState(light, lightState);
            }
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("http://redgreenlight.herokuapp.com/setstate/1");

        }

    public void offLights(List<PHLight> allLights, PHBridge bridge) {
            discoTask.cancel(true);
            for (PHLight light : allLights) {
                PHLightState lightState = new PHLightState();
                lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                lightState.setTransitionTime(1);
                lightState.setOn(false);
                bridge.updateLightState(light, lightState);
            }
        }

    // test code for pebble
    public void blinkLights(List<PHLight> allLights, PHBridge bridge) {
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_SELECT);
            bridge.updateLightState(light, lightState);
        }
    }

    public void discoLights(List<PHLight> allLights, PHBridge bridge) {
        discoTask.execute(allLights, bridge);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("http://redgreenlight.herokuapp.com/setstate/1337");
    }

    // async task - victory condition
    public class DiscoTask extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... param) {
            List<PHLight> allLights = (List<PHLight>) param[0];
            PHBridge bridge = (PHBridge) param[1];

            // disco logic
            Random rand = new Random();
            while (!isCancelled()) {
                for (PHLight light : allLights) {
                    PHLightState lightState = new PHLightState();
                    lightState.setOn(true);
                    lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_SELECT);
                    lightState.setTransitionTime(1);
                    lightState.setBrightness(225);
                    lightState.setHue(rand.nextInt(MAX_HUE));
                    bridge.updateLightState(light, lightState);
                    android.os.SystemClock.sleep(100);
                }
            }

            return true;
        }

    }

    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
