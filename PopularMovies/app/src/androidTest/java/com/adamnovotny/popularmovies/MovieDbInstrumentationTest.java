package com.adamnovotny.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.adamnovotny.popularmovies.data.MovieDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testDbCreate() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    @Test
    public void testDbInsert() {
        MovieDbHelper db = new MovieDbHelper(mContext);
        Long row = db.insertFavorite("12345");
        Long notExpected = -1L;
        assertNotSame(row, notExpected);
        db.eraseAllFavorite();
    }

    @Test
    public void testDbQuery() {
        String[] id = {"12345", "6789"};
        MovieDbHelper db = new MovieDbHelper(mContext);
        Long row = db.insertFavorite(id[0]);
        Long notExpected = -1L;
        assertNotSame(row, notExpected);
        row = db.insertFavorite(id[1]);
        assertNotSame(row, notExpected);
        ArrayList<String> out = db.getAllFavorite();
        for (int i = 0; i<out.size(); i++) {
            assertEquals(id[i], out.get(i));
        }
        db.eraseAllFavorite();
    }

}
