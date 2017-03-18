package com.example.android.sunshine;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private String LOG_TAG = WearableService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    public WearableService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Wearable connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        Log.d(LOG_TAG, " running from onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "mGoogleApiClient disconnected");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(LOG_TAG, "Data changed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(LOG_TAG, "Message Received 2");
        Log.d(LOG_TAG, "Path: " + messageEvent.getPath());
        if (messageEvent.getPath().equals("/weather-update")) {
            final String message = new String(messageEvent.getData());
            Log.d(LOG_TAG, "Message Received: " + message);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.d(LOG_TAG, "mConnection OK 3");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "mConnection failed " + connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "mConnection suspended");
    }
}
