package com.example.android.sunshine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.sunshine.R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(com.example.android.sunshine.R.id.watch_view_stub);
        /*
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                TextView mTextView = (TextView) stub.findViewById(com.example.android.sunshine.R.id.text);
            }
        });
        */

        Intent iService = new Intent(getApplicationContext(), WearableService.class);
        startService(iService);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ArrayList<String> weatherAl = intent.getStringArrayListExtra(
                                WearableService.WEATHER_BROADCAST_ARRAY_LIST);
                        ArrayList<Integer> weatherViews = new ArrayList<Integer>();
                        weatherViews.add(R.id.time_tv);
                        weatherViews.add(R.id.date_tv);
                        weatherViews.add(R.id.weather_icon);
                        weatherViews.add(R.id.high_temperature);
                        weatherViews.add(R.id.low_temperature);
                        for (int i = 0; i < weatherAl.size(); i++) {
                            Log.d(LOG_TAG, "Received broadcast " + i + ":" + weatherAl.get(i));
                            // update text views
                            if (i != 2) {
                                TextView mTextView = (TextView) findViewById(weatherViews.get(i));
                                mTextView.setText(weatherAl.get(i));
                            }
                        }
                        // update image view
                        ImageView mImageView = (ImageView) findViewById(weatherViews.get(2));

                        HashMap<String, Integer> hm = new HashMap();
                        hm.put("art_clear", R.drawable.art_clear);
                        hm.put("art_clouds", R.drawable.art_clouds);
                        hm.put("art_fog", R.drawable.art_fog);
                        hm.put("art_light_clouds", R.drawable.art_light_clouds);
                        hm.put("art_light_rain", R.drawable.art_light_rain);
                        hm.put("art_rain", R.drawable.art_rain);
                        hm.put("art_snow", R.drawable.art_snow);
                        hm.put("art_storm", R.drawable.art_storm);
                        mImageView.setImageDrawable(
                                ContextCompat.getDrawable(getApplicationContext(),
                                        hm.get(weatherAl.get(2)))
                        );

                    }
                }, new IntentFilter(WearableService.WEATHER_BROADCAST_URI)
        );
    }
}
