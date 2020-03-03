package com.example.livespeedcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView textView = null;
    TelephonyManager telephonyManager;
    MyPhoneStateListener phoneStateListener;
    int mSignalStrength = 0;
    long currTime = 0;
    double dtHours = 0;
    double dtDistance = 0;
    double currPorWatts = 0;
    double distance = 0;
    int l=0;
    int pt =1000;
    public static final String APP_NAME = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.speed);
        currTime = Calendar.getInstance().getTimeInMillis();
        phoneStateListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }


    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            dtHours = timeCalc();
            mSignalStrength = signalStrength.getGsmSignalStrength();
            mSignalStrength = (2 * mSignalStrength) - 113; // -> dBm
            dtDistance = currDistance() - distance;
            distance = currDistance();
            double speedNum = dtDistance/dtHours;
            textView.setText(String.valueOf(speedNum)+" km/h");
            Log.i(APP_NAME, String.valueOf(mSignalStrength));
        }
    }

    private double timeCalc (){
        long nextTime = Calendar.getInstance().getTimeInMillis();
        long dt = nextTime - currTime;
        currTime = nextTime;
        double dtHours = dt*0.000000278;
        return dtHours;
    }

    private double p2watt(){
        double Por = ((10 ^ (mSignalStrength / 10)) /(0.001));
        return Por;
    }

    private double currDistance(){
        currPorWatts = p2watt();
        double d = (l/ (4*Math.PI)) * Math.sqrt(pt/ currPorWatts);
        return d;
    }
}
