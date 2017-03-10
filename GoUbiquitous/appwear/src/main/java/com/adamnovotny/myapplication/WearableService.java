package com.adamnovotny.myapplication;

import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableService extends WearableListenerService {

    private String LOG_TAG = WearableService.class.getSimpleName();

    public WearableService() {
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath() == "/update-weather") {
            Log.d(LOG_TAG, "Message Received");
        }
    }
}
