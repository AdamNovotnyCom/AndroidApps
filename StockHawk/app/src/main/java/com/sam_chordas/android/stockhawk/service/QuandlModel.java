package com.sam_chordas.android.stockhawk.service;

import java.util.ArrayList;

/**
 * Retrofit model for Quandl.com stock price calls to
 * to www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date.gte=[yyyymmdd]&ticker=[ticker]&qopts.columns=date,close&api_key=[key]
 */
public class QuandlModel {
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<Integer> prices = new ArrayList<Integer>();

    // getters and setters
    public ArrayList<String> getDates() {
        return dates;
    }
    public ArrayList<Integer> getPrices() {
        return prices;
    }
    public void addDate(String d) {
        dates.add(d);
    }
    public void addPrice(String p) {
        Double priceD = Double.parseDouble(p);
        prices.add(priceD.intValue());
    }
}
