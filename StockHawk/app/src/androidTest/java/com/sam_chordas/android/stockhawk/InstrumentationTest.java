package com.sam_chordas.android.stockhawk;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InstrumentationTest extends ActivityInstrumentationTestCase2 {
    Context mContext;
    String LOG_TAG = getClass().getSimpleName();

    public InstrumentationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testInsertProvider() {

    }
}
