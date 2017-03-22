package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
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
    private NodeApi.GetConnectedNodesResult ApiNodes;
    private Calendar calendar;
    private String weatherImage;
    private String weatherHigh;
    private String weatherLow;
    private Context mContext;
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
                list.add(weatherImage);
                list.add(weatherHigh);
                list.add(weatherLow);

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
                        getWeatherCursor();
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

    private void getWeatherCursor() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                "date = " + getTodayDate().toString(),
                null,
                null);
        cursor.moveToFirst();
        weatherImage = getWeatherImage(cursor);
        weatherHigh = getTemperatureHigh(cursor);
        weatherLow = getTemperatureLow(cursor);
    }

    private Long getTodayDate() {
        Long epoch = 0L;
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(new SimpleTimeZone(0, "GMT"));
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd yyyy");
        format1.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String formattedDate = format1.format(cal.getTime());
        try {
            Date date = format1.parse(formattedDate);
            epoch = date.getTime();
            Log.d(TAG, "Date is format today: " + epoch);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return epoch;
    }

    private String getWeatherImage(Cursor cursor) {
        int weatherIdCol = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        Integer weatherId = Integer.parseInt(cursor.getString(weatherIdCol));
        if (weatherId >= 200 && weatherId <= 232) {
            return "art_storm";
        } else if (weatherId >= 300 && weatherId <= 321) {
            return "art_light_rain";
        } else if (weatherId >= 500 && weatherId <= 504) {
            return "art_rain";
        } else if (weatherId == 511) {
            return "art_snow";
        } else if (weatherId >= 520 && weatherId <= 531) {
            return "art_rain";
        } else if (weatherId >= 600 && weatherId <= 622) {
            return "art_snow";
        } else if (weatherId >= 701 && weatherId <= 761) {
            return "art_fog";
        } else if (weatherId == 761 || weatherId == 771 || weatherId == 781) {
            return "art_storm";
        } else if (weatherId == 800) {
            return "art_clear";
        } else if (weatherId == 801) {
            return "art_light_clouds";
        } else if (weatherId >= 802 && weatherId <= 804) {
            return "art_clouds";
        } else if (weatherId >= 900 && weatherId <= 906) {
            return "art_storm";
        } else if (weatherId >= 958 && weatherId <= 962) {
            return "art_storm";
        } else if (weatherId >= 951 && weatherId <= 957) {
            return "art_clear";
        }
        return "art_clear";
    }

    private String getTemperatureHigh(Cursor cursor) {
        int maxTempIdCol = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        Long temp = Math.round(Double.parseDouble(cursor.getString(maxTempIdCol)));
        return temp.toString();
    }

    private String getTemperatureLow(Cursor cursor) {
        int minTempIdCol = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        Long temp = Math.round(Double.parseDouble(cursor.getString(minTempIdCol)));
        return temp.toString();
    }
}
