package com.adamnovotny.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adamnovotny.popularmovies.data.MovieContract.MovieEntry;

import java.util.ArrayList;

/**
 * Used to manage local database
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";
    private Context mContext;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This only fires if you change the version number for your database.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /*
     * CRUD operations. To be replaced by a ContentProvider
     */
    public long insertFavorite(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        long row = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        db.close();
        return row;
    }

    public ArrayList<String> getAllFavorite() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        ArrayList<String> favorites = new ArrayList<>();
        int idCol = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
        if (cursor.moveToFirst()) {
            favorites.add(cursor.getString(idCol));
            while (cursor.moveToNext()) {
                favorites.add(cursor.getString(idCol));
            }
        }
        cursor.close();
        db.close();
        return favorites;
    }

    public void eraseAllFavorite() {
        // This only fires if you change the version number for your database.
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
        db.close();
    }
}
