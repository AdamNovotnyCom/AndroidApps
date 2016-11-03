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

import java.util.ArrayList;
import java.util.List;

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
        getData();
        generatePriceChart ();
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
        //https://github.com/lecho/hellocharts-android
        /*
        LineChartView chart = (LineChartView) findViewById(R.id.stock_chart);

        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        chart.setLineChartData(data);

        */


        // style
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
        // add data
        List<String> xlabels = new ArrayList<String>();
        List<Entry> entries = new ArrayList<Entry>();
        for (Integer i = 0; i < 100; i++) {
            entries.add(new Entry(i, i*i));
            xlabels.add(i.toString());
        }
        LineDataSet dataSet = new LineDataSet(entries, "Price ($)");
        dataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        dataSet.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        dataSet.setValueTextColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        LineData lineData = new LineData(dataSet);

        String[] labelsArr = new String[xlabels.size()];
        labelsArr = xlabels.toArray(labelsArr);
        x1.setValueFormatter(new MyXAxisValueFormatter(labelsArr));

        chart.setData(lineData);
        chart.invalidate(); // refresh

    }

    private void getData() {
        // https://www.quandl.com/api/v3/datasets/WIKI/HLF.json
    }
}

class MyXAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        //return mValues[(int) value] + 'b';
        return "a";
    }

    /** this is only needed if numbers are returned, else return 0 */
    @Override
    public int getDecimalDigits() { return 0; }
}