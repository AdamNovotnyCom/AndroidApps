package com.sam_chordas.android.stockhawk.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class StockDetailActivity extends AppCompatActivity {
    String symbol;
    ContentResolver resolver;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        mContext = this;

        // get symbol from position
        int position = getIntent().getExtras().getInt("position");
        resolver = this.getContentResolver();
        Cursor c = resolver.query(
                QuoteProvider.Quotes.CONTENT_URI,
                new String[] { QuoteColumns.SYMBOL },
                QuoteColumns.ISCURRENT + "= 1",
                null,
                null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            int count = 0;
            int symbolIdx = c.getColumnIndex(QuoteColumns.SYMBOL);
            symbol = c.getString(symbolIdx);
            if (count != position) {
                while (c.moveToNext()) {
                    count++;
                    if (count == position) {
                        symbol = c.getString(symbolIdx);
                    }
                }
            }
        }

        // listeners
        setupListeners();
    }


    private void setupListeners() {
        Button deleteBtn = (Button) findViewById(R.id.delete_stock_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String where = QuoteColumns.SYMBOL + "=?";
                String[] args = new String[] {symbol};
                resolver.delete( QuoteProvider.Quotes.CONTENT_URI, where, args );

                Toast.makeText(mContext,
                        "Deleted symbol: " + symbol,
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, MyStocksActivity.class);
                startActivity(intent);
            }
        });
    }

    private void generatePriceChart () {
        // https://www.quandl.com/api/v3/datasets/WIKI/HLF.json
    }
}
