package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.WidgetRemoteViewsService;

public class StockWidgetProvider extends AppWidgetProvider {
    private String LOG_TAG = StockWidgetProvider.class.getSimpleName();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // setup collection
            views.setRemoteAdapter(R.id.widget_list_view,
                    new Intent(context, WidgetRemoteViewsService.class));
            Intent intentDetail = new Intent(context, StockDetailActivity.class);
            PendingIntent pendingIntentDetail = PendingIntent.getActivity(context, 0, intentDetail, 0);
            views.setPendingIntentTemplate(R.id.widget_list_view, pendingIntentDetail);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (StockIntentService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Log.i(LOG_TAG, " widget received new data ");

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        }
    }
}