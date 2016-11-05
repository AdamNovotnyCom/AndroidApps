package com.sam_chordas.android.stockhawk.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.TestData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockDetailActivity extends AppCompatActivity {
    final String LOG_TAG = StockDetailActivity.class.getSimpleName();
    ContentResolver resolver;
    Context mContext;
    String symbol;
    String pricesJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getData();
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

    private void getData() {
        // https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date.gte=20150101&ticker=TSLA&qopts.columns=date,close&api_key=[TBU]
        // TODO call to quandl
        pricesJson = TestData.tslaPrices; ////// remove
        ArrayList<ArrayList<String>> pricesAL = parseJson(pricesJson);
        generatePriceChart(pricesAL);
    }

    private ArrayList<ArrayList<String>> parseJson(String pricesJson) {
        // inner ArrayList formatted as {date, price}
        ArrayList<ArrayList<String>> pricesAL = new ArrayList<ArrayList<String>>();
        if (pricesJson != null) {
            try {
                JSONObject jsonObj = new JSONObject(pricesJson);
                JSONObject datatable = jsonObj.getJSONObject("datatable");
                JSONArray data = datatable.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String date = data.getJSONArray(i).getString(0);
                    String price = data.getJSONArray(i).getString(1);
                    ArrayList<String> day = new ArrayList<>();
                    day.add(date);
                    day.add(price);
                    pricesAL.add(day);
                }
            }
            catch (final JSONException e) {
                Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
            }
        }
        return pricesAL;
    }

    /**
     * Uses https://github.com/PhilJay/MPAndroidChart
     * @param pricesAL ArrayList<String>[date, price]
     */
    private void generatePriceChart(ArrayList<ArrayList<String>> pricesAL) {
        //https://github.com/lecho/hellocharts-android
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
        List<String> xlabels = new ArrayList<String>();
        List<Entry> entries = new ArrayList<Entry>();
        for (Integer i = 0; i < pricesAL.size(); i++) {
            Double priceD = Double.parseDouble(pricesAL.get(i).get(1));
            Integer priceI = priceD.intValue();
            entries.add(new Entry(i, priceI));
            xlabels.add(pricesAL.get(i).get(0));
        }
        // data style
        LineDataSet dataSet = new LineDataSet(entries, "Price " + symbol + " ($)");
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


/**
 * Helper class to format hellocharts x-axis
 */
class MyXAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }

    /** this is only needed if numbers are returned, else return 0 */
    @Override
    public int getDecimalDigits() { return 0; }
}