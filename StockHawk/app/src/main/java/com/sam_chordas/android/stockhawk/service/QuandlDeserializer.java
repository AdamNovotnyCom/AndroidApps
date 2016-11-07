package com.sam_chordas.android.stockhawk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserializes stock prices from Quandl.com
 */
public class QuandlDeserializer implements JsonDeserializer<QuandlModel> {
    @Override
    public QuandlModel deserialize(final JsonElement json, final Type typeOfT,
                                   final JsonDeserializationContext context)
            throws JsonParseException {
        final QuandlModel qm = new QuandlModel();
        // parse json
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonObject jsonDatatable = jsonObject.get("datatable").getAsJsonObject();
        final JsonArray jsonDayArray = jsonDatatable.get("data").getAsJsonArray();
        for (int i = 0; i < jsonDayArray.size(); i++) {
            JsonArray day = jsonDayArray.get(i).getAsJsonArray();
            qm.addDate(day.get(0).getAsString());
            qm.addPrice(day.get(1).getAsString());
        }
        return qm;

    }
}
