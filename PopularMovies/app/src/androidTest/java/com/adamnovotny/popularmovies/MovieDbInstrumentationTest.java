package com.adamnovotny.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.adamnovotny.popularmovies.data.MovieDbHelper;
import com.facebook.stetho.Stetho;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MovieDbInstrumentationTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    Context mContext;

    public MovieDbInstrumentationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        this.mContext = getActivity();
        Stetho.initializeWithDefaults(mContext);
    }

    @Test
    public void testDbCreate() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
