package com.adamnovotny.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.adamnovotny.popularmovies.data.MovieContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProviderTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    Context mContext;
    String LOG_TAG = getClass().getSimpleName();

    public ProviderTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        this.mContext = getActivity();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryProvider() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor.getCount();
        assertNotSame(count, 0);
        int favoriteIdCol = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            Log.i(LOG_TAG, cursor.getString(favoriteIdCol));
        }
    }

}
