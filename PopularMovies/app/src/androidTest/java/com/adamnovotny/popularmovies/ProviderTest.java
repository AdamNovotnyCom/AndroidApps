package com.adamnovotny.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

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
    public void testInsertProvider() {
        // insert
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        String testId = "123";
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, testId);
        Uri uri = resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
        uri = Uri.parse(uri.toString());
        String protocol = uri.getScheme();
        String auth = uri.getAuthority();
        assertEquals("content", protocol);
        assertEquals(auth, MovieContract.CONTENT_AUTHORITY);
        // delete
        String delStr = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + testId;
        int rows = resolver.delete(MovieContract.MovieEntry.CONTENT_URI, delStr, null);
        assertTrue(rows >= 1);
    }

    @Test
    public void testQueryProvider() {
        ContentResolver resolver = mContext.getContentResolver();
        // insert
        String testId = "123";
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, testId);
        Uri uri = resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
        // query
        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor.getCount();
        assertNotSame(count, 0);
        int favoriteIdCol = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        cursor.moveToFirst();
        assertEquals(testId, cursor.getString(favoriteIdCol));
        // delete
        String delStr = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + testId;
        int rows = resolver.delete(MovieContract.MovieEntry.CONTENT_URI, delStr, null);
        assertTrue(rows >= 1);
    }

    @Test
    public void testUpdateProvider() {
        // insert
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        String testId = "123";
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, testId);
        Uri uri = resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
        // modify
        String testId2 = "345";
        values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, testId2);
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + testId;
        int rows = resolver.update(
                MovieContract.MovieEntry.CONTENT_URI, values, selection, null);
        assertTrue(rows >= 1);
        // delete
        String delStr = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + testId2;
        rows = resolver.delete(MovieContract.MovieEntry.CONTENT_URI, delStr, null);
        assertTrue(rows >= 1);
    }
}
