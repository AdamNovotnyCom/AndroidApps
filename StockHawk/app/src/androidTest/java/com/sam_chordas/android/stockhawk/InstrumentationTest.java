package com.sam_chordas.android.stockhawk;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InstrumentationTest extends ActivityInstrumentationTestCase2 {
    Context mContext;
    String LOG_TAG = getClass().getSimpleName();

    public InstrumentationTest() {
        super(MyStocksActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mContext = getActivity();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testInsert() {
        ContentResolver resolver = getActivity().getContentResolver();
        // insert
        ContentValues values = new ContentValues();
        values.put(QuoteColumns.SYMBOL, "testSym");
        values.put(QuoteColumns.BIDPRICE, 10);
        values.put(QuoteColumns.CHANGE, 1);
        values.put(QuoteColumns.PERCENT_CHANGE, 0.1);
        values.put(QuoteColumns.ISUP, 1);
        values.put(QuoteColumns.ISCURRENT, 1);
        Uri uri = resolver.insert(QuoteProvider.Quotes.CONTENT_URI, values);

        // query
        Cursor c = resolver.query(
                QuoteProvider.Quotes.CONTENT_URI,
                new String[] { QuoteColumns.SYMBOL },
                QuoteColumns.SYMBOL + "= ?",
                new String[] { "testSym" },
                null);
        assertEquals(true, c.getCount() > 0);

        // delete
        String where = QuoteColumns.SYMBOL + "=?";
        String[] args = new String[] { "testSym" };
        resolver.delete( QuoteProvider.Quotes.CONTENT_URI, where, args );

        // query
        c = resolver.query(
                QuoteProvider.Quotes.CONTENT_URI,
                new String[] { QuoteColumns.SYMBOL },
                QuoteColumns.SYMBOL + "= ?",
                new String[] { "testSym" },
                null);
        assertEquals(true, c.getCount() == 0);
    }


}
