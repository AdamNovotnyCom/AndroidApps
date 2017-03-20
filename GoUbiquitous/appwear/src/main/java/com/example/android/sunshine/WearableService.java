package com.example.android.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.gms.wearable.DataMap.TAG;

public class WearableService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private NodeApi.GetConnectedNodesResult ApiNodes;
    public static final String WEATHER_BROADCAST_ARRAY_LIST = "Weather_Al";
    public static final String WEATHER_BROADCAST_URI = WearableService.class.getName() +
            "WeatherUpdate";
    public static final String WEATHER_REQUEST_BROADCAST_URI = WearableService.class.getName() +
            "WeatherRequestUpdate";

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
        receiveBroadcastInit();
        requestDataFromMobile();
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
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "mConnection failed " + connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "mConnection suspended");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/weather-update")) {
            ByteArrayInputStream bais = new ByteArrayInputStream(messageEvent.getData());
            DataInputStream in = new DataInputStream(bais);
            try {
                ArrayList<String> weatherAl = new ArrayList<>();
                while (in.available() > 0) {
                    String element = in.readUTF();
                    weatherAl.add(element);
                }
                sendBroadcast(weatherAl);
            }
            catch (Exception e) {
                Log.d(LOG_TAG, e.toString());
            }
        }
    }

    /**
        Sends broadcast to UI thread
     */
    private void sendBroadcast(ArrayList<String> weatherAl) {
        Intent intent = new Intent(WEATHER_BROADCAST_URI);
        intent.putStringArrayListExtra(WEATHER_BROADCAST_ARRAY_LIST, weatherAl);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     Receive broadcast from UI thread
     */
    private void receiveBroadcastInit() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getStringExtra("DataRequest").equals("DataRequestMessage")) {
                            requestDataFromMobile();
                        }
                    }
                }, new IntentFilter(WEATHER_REQUEST_BROADCAST_URI)
        );
    }

    /**
     Get data from mobile
     */
    public void requestDataFromMobile() {
        Observer<String> stringObserver;
        stringObserver = new Observer<String>() {
            @Override
            public void onNext(String value) {

                if (value.equals("Complete")) {
                    for (Node node : ApiNodes.getNodes()) {
                        Wearable.MessageApi.sendMessage(
                                mGoogleApiClient, node.getId(), "/request-weather-update", "RequestData".getBytes())
                                .setResultCallback(
                                        new ResultCallback<MessageApi.SendMessageResult>() {
                                            @Override
                                            public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                                                if (!sendMessageResult.getStatus().isSuccess()) {
                                                    Log.d(TAG, "Failed to send message");
                                                }
                                            }
                                        }
                                );
                    }
                }
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d("stringObserver", "onError", e);
            }
        };
        Observable observable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber subscriber) {
                        ApiNodes = Wearable.NodeApi
                                .getConnectedNodes(mGoogleApiClient).await();
                        subscriber.onNext("Complete");
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread
                .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread
        observable.subscribe(stringObserver);
    }
}
