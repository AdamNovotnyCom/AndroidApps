package com.example.android.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;
    private String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.sunshine.R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(com.example.android.sunshine.R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(com.example.android.sunshine.R.id.text);
            }
        });

        Intent iService = new Intent(getApplicationContext(), WearableService.class);
        startService(iService);
    }
}
