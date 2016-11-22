package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class WidgetRemoteViewsService extends RemoteViewsService {
    private String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // null
            }

            @Override
            public void onDataSetChanged() {
                Log.i(LOG_TAG, " widget updated ");
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        new String[] {
                                QuoteColumns.SYMBOL,
                                QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE},
                        QuoteColumns.ISCURRENT + "= 1",
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                views.setTextViewText(R.id.stock_symbol, data.getString(0));
                views.setTextViewText(R.id.bid_price, data.getString(1));
                views.setTextViewText(R.id.change, data.getString(2));

                // set on click listener. Pending listener set on collection in
                // StockWidgetProvider
                Bundle extras = new Bundle();
                extras.putInt("position", position);
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.widget_list_item_row, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(0);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
