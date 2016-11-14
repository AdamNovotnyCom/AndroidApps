package com.sam_chordas.android.stockhawk.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.AppKeys;
import com.sam_chordas.android.stockhawk.rest.MyXAxisValueFormatter;
import com.sam_chordas.android.stockhawk.service.QuandlDeserializer;
import com.sam_chordas.android.stockhawk.service.QuandlModel;
import com.sam_chordas.android.stockhawk.service.StockPricesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StockDetailActivity extends AppCompatActivity {
    final String LOG_TAG = StockDetailActivity.class.getSimpleName();
    ContentResolver resolver;
    Context mContext;
    int mRefreshCount = 0; // refresh api if > 0
    String symbol;
    String yearSince = "2015";
    String dateSince = yearSince + "0101";
    ArrayList<String> datesAL;
    ArrayList<Integer> pricesAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("symbol")) {
            symbol = savedInstanceState.getString("symbol");
            dateSince = savedInstanceState.getString("dateSince");
            datesAL = savedInstanceState.getStringArrayList("dates");
            pricesAL = savedInstanceState.getIntegerArrayList("prices");
            mRefreshCount = 2;
        }
        setContentView(R.layout.activity_stock_detail);
        mContext = this;

        // get symbol from position in MyStocksActivity
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

        setupListeners();
    }

    /**
     * @param outState is generated when app is closed.
     * data is restored using savedInstanceState in onCreate()
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("symbol", symbol);
        outState.putString("dateSince", dateSince);
        outState.putStringArrayList("dates", datesAL);
        outState.putIntegerArrayList("prices", pricesAL);
    }


    private void setupListeners() {
        Button deleteBtn = (Button) findViewById(R.id.delete_stock_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String where = QuoteColumns.SYMBOL + "=?";
                String[] args = new String[]{symbol};
                resolver.delete(QuoteProvider.Quotes.CONTENT_URI, where, args);

                Toast.makeText(mContext,
                        "Deleted symbol: " + symbol,
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, MyStocksActivity.class);
                startActivity(intent);
            }
        });

        // spinner
        final Spinner spinner = (Spinner) findViewById(R.id.year_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                yearSince = spinner.getSelectedItem().toString();
                dateSince = yearSince + "0101";
                String message = getResources().getString(R.string.chart_start_toast) + " " + yearSince;
                Toast.makeText(mContext, message,
                        Toast.LENGTH_LONG).show();
                retroQuandlCall();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
    }

    private void retroQuandlCall() {
        if (mRefreshCount > 0) {
            generatePriceChart();
            mRefreshCount--;
        }
        else {
            Observer<String> stringObserver;
            stringObserver = new Observer<String>() {
                @Override
                public void onNext(String value) {
                    if (value.equals("OK")) {
                        generatePriceChart();
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
                            Gson gson = new GsonBuilder().registerTypeAdapter(
                                    QuandlModel.class, new QuandlDeserializer()).create();
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://www.quandl.com")
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();
                            StockPricesService priceService = retrofit.create(
                                    StockPricesService.class);
                            Call<QuandlModel> call = priceService.getStockPrices(
                                    dateSince,
                                    symbol,
                                    "date,close",
                                    AppKeys.quandlKey
                            );
                            try {
                                QuandlModel pricesData = call.execute().body();
                                datesAL = pricesData.getDates();
                                pricesAL = pricesData.getPrices();
                                Log.i(LOG_TAG, "Prices fetched from Api");
                                subscriber.onNext("OK");
                                subscriber.onCompleted();
                            } catch (IOException e) {
                                Log.e(LOG_TAG, e.toString());
                            }

                        }
                    })
                    .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread
                    .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread
            observable.subscribe(stringObserver);
        }
    }

    /**
     * Uses https://github.com/PhilJay/MPAndroidChart
     */
    private void generatePriceChart() {
        if (datesAL.size() == 0) {
            Toast toast = Toast.makeText(
                    mContext,
                    getResources().getString(R.string.no_price_data_toast), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
            toast.show();
            return;
        }
        // chart style
        LineChart chart = (LineChart) findViewById(R.id.stock_chart);
        int baseColor = ColorTemplate.LIBERTY_COLORS[0];
        chart.getLegend().setTextColor(baseColor);
        chart.getLegend().setTextSize(16);
        chart.getDescription().setText("");
        YAxis y1 = chart.getAxisLeft();
        y1.setTextSize(14);
        y1.setTextColor(baseColor);
        y1.setAxisLineColor(baseColor);
        YAxis y2 = chart.getAxisRight();
        y2.setTextSize(14);
        y2.setTextColor(baseColor);
        y2.setAxisLineColor(baseColor);
        XAxis x1 = chart.getXAxis();
        x1.setTextSize(14);
        x1.setTextColor(baseColor);
        x1.setAxisLineColor(baseColor);
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
        x1.setGranularity((Integer) pricesAL.size() / 3);
        // add data
        List<String> xlabels = datesAL;
        List<Entry> entries = new ArrayList<Entry>();
        for (Integer i = 0; i < pricesAL.size(); i++) {
            entries.add(new Entry(i, pricesAL.get(i)));
        }
        // data style
        LineDataSet dataSet = new LineDataSet(entries,
                getResources().getString(R.string.price_word) + " " + symbol);
        dataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        dataSet.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        dataSet.setValueTextColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        // format x-axis
        String[] labelsArr = new String[xlabels.size()];
        labelsArr = xlabels.toArray(labelsArr);
        x1.setValueFormatter(new MyXAxisValueFormatter(labelsArr));
        // draw chart
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}