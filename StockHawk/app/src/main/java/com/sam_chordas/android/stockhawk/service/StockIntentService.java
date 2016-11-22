package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {
    final String LOG_TAG = StockIntentService.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "com.sam_chordas.android.stockhawk.ACTION_DATA_UPDATED";

    public StockIntentService(){
    super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
    super(name);
    }

    @Override protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra("tag") == null) {
            return;
        }
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")){
          args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        Integer result = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
        // error data fetch
        if (result == 2) {
            // Stock symbol invalid
            Observable observable = Observable.create(
                    new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber subscriber) {
                            subscriber.onNext("invalidTicker");
                            subscriber.onCompleted();
                        }
                    })
                    .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread
                    .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread
            observable.subscribe(MyStocksActivity.stringObserver);
        }
        // success data fetch
        else if (result == 0) {
            // update widgets
            updateWidgets();
        }
    }

    private void updateWidgets() {
        Context context = this;
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
