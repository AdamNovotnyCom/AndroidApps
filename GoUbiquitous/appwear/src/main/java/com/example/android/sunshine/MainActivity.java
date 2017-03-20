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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.NodeApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import static com.example.android.sunshine.WearableService.WEATHER_BROADCAST_ARRAY_LIST;
import static com.example.android.sunshine.WearableService.WEATHER_BROADCAST_URI;

public class MainActivity extends Activity {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private NodeApi.GetConnectedNodesResult ApiNodes;
    private Calendar calendar;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.sunshine.R.layout.activity_main);

        Intent iService = new Intent(getApplicationContext(), WearableService.class);
        startService(iService);

        // receive weather update pushed from phone to WearableService
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ArrayList<String> weatherAl = intent.getStringArrayListExtra(
                                WEATHER_BROADCAST_ARRAY_LIST);
                        /*
                        weatherAl.get(0) = phone time, not necessary if wear setDateAndTime is used
                        weatherAl.get(1) = phone date, not necessary if wear setDateAndTime is used
                        */
                        // update high
                        TextView mTextView = (TextView) findViewById(R.id.high_temperature);
                        mTextView.setText(weatherAl.get(3));
                        // update low
                        mTextView = (TextView) findViewById(R.id.low_temperature);
                        mTextView.setText(weatherAl.get(4));

                        // update image view
                        ImageView mImageView = (ImageView) findViewById(R.id.weather_icon);

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
                }, new IntentFilter(WEATHER_BROADCAST_URI)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(com.example.android.sunshine.R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                setDateAndTime();
            }
        });
        requestWeatherData();
    }

    private void requestWeatherData() {
        Intent intent = new Intent(WearableService.WEATHER_REQUEST_BROADCAST_URI);
        intent.putExtra("DataRequest", "DataRequestMessage");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void setDateAndTime() {
        calendar = new GregorianCalendar(TimeZone.getDefault());
        TextView mTimeTextView = (TextView) findViewById(R.id.time_tv);
        TextView mDateTextView = (TextView) findViewById(R.id.date_tv);
        mTimeTextView.setText(getTime());
        mDateTextView.setText(getDate());
    }

    private String getTime() {
        String hour = ((Integer) calendar.get(Calendar.HOUR_OF_DAY)).toString();
        String minute = ((Integer) calendar.get(Calendar.MINUTE)).toString();
        if (Integer.parseInt(minute) < 10) {
            minute = "0" + minute;
        }
        return hour + ":" + minute;
    }

    private String getDate() {
        SimpleDateFormat format1 = new SimpleDateFormat("EEE, MMM dd yyyy");
        String formatted_date = format1.format(calendar.getTime());
        return formatted_date;
    }
}
