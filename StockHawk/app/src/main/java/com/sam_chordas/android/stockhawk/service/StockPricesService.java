package com.sam_chordas.android.stockhawk.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockPricesService {
    // www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date.gte=[yyyymmdd]&ticker=[ticker]&qopts.columns=date,close&api_key=[key]
    // Example Api call https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date.gte=20150101&ticker=TSLA&qopts.columns=date,close&api_key=[key]
    @GET("/api/v3/datatables/WIKI/PRICES.json")
    Call<QuandlModel> getStockPrices(
            @Query("date.gte") String date,
            @Query("ticker") String ticker,
            @Query("qopts.columns") String columns,
            @Query("api_key") String apiKey);
}
