package com.example.android.sunshine.sync;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Helper class used to update Android Wear devices with weather data
 */

public class UpdateWear implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "UpdateWear";
    private Calendar calendar;
    private Context mContext;
    private NodeApi.GetConnectedNodesResult ApiNodes;
    private GoogleApiClient mGoogleApiClient;

    public UpdateWear(Context mC) {
        mContext = mC;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        setCalendar();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "mConnected to api");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "mConnected failed" + connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "mConnected suspended");
    }

    public void sendWeatherMessageToWear() {
        Observer<String> stringObserver;
        stringObserver = new Observer<String>() {
            @Override
            public void onNext(String value) {
                // weather data to be sent when observable
                // locates wear devices
                List<String> list = new ArrayList<String>();
                list.add(getTime());
                list.add(getDate());
                list.add("art_storm");
                list.add("high");
                list.add("low");

                // write to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(baos);
                for (String element : list) {
                    try {
                        out.writeUTF(element);
                    }
                    catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
                byte[] bytes = baos.toByteArray();

                if (value.equals("Complete")) {
                    for (Node node : ApiNodes.getNodes()) {
                        Wearable.MessageApi.sendMessage(
                                mGoogleApiClient, node.getId(), "/weather-update", bytes)
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
                        Log.d(TAG, "Message sent: " + node.getId());
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

    private void setCalendar() {
        calendar = new GregorianCalendar(TimeZone.getDefault());
    }

    private String getTime() {
        String hour = ((Integer) calendar.get(Calendar.HOUR_OF_DAY)).toString();
        String minute = ((Integer) calendar.get(Calendar.MINUTE)).toString();
        return hour + ":" + minute;
    }

    private String getDate() {
        SimpleDateFormat format1 = new SimpleDateFormat("EEE, MMM dd yyyy");
        String formatted_date = format1.format(calendar.getTime());
        return formatted_date;
    }
}
