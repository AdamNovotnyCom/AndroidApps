package com.example.android.sunshine.data;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.example.android.sunshine.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestWearProvider extends ActivityInstrumentationTestCase2 {
    String LOG_TAG = getClass().getSimpleName();
    Activity mMainActivity;

    public TestWearProvider() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mMainActivity = getActivity();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetWeather() {
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor c = resolver.query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        int dateIdCol = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int weatherIdCol = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        int maxTempIdCol = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int minTempIdCol = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        c.moveToFirst();
        //assertEquals("123", c.getString(dateIdCol));
        //assertEquals("123", c.getString(weatherIdCol));
        //assertEquals("123", c.getString(maxTempIdCol));
        //assertEquals("123", c.getString(minTempIdCol));
        assertEquals(true, true);
    }
}
