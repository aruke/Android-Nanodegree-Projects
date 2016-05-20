package me.rajanikant.movies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.beans.IndexedPropertyChangeEvent;
import java.util.concurrent.ExecutionException;

import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.api.model.MoviesTable;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 20 May 2016
 */
public class ProviderTest extends ApplicationTest{

    /**
     *  Note : Tests have to be ran separately and one by one in order of appearance
     */


    private static final String TAG = "ProviderTest";

    private Movie movie;

    @Override
    public void setUp() throws Exception {

        movie = new Movie();
        movie.setId(1);
        movie.setTitle("SuperDuper Movie");
        movie.setOriginalLanguage("en");
        movie.setOriginalTitle("SuperDuper Movie");

    }

    public void testInsert() throws Exception{

        ContentResolver contentResolver = mContext.getContentResolver();

        Uri returnUri = contentResolver.insert(MoviesTable.CONTENT_URI, MoviesTable.getContentValues(movie, true));

        long l = ContentUris.parseId(returnUri);

        assertEquals(l, 1);
    }

    public void testQuery() throws Exception {

        ContentResolver contentResolver = mContext.getContentResolver();

        assertNotNull(contentResolver);

        Cursor cursor = contentResolver.query(MoviesTable.CONTENT_URI, null, null, null, null);

        assertNotNull(cursor);

        int COLUMN_ID = cursor.getColumnIndex(MoviesTable.FIELD_ID);
        int COLUMN_TITLE = cursor.getColumnIndex(MoviesTable.FIELD_TITLE);
        int COLUMN_ORIGINAL_LANG = cursor.getColumnIndex(MoviesTable.FIELD_ORIGINAL_LANGUAGE);

        Log.d(TAG, "testQuery: ColumnId " + COLUMN_ID);
        Log.d(TAG, "testQuery: ColumnTitle " + COLUMN_TITLE);
        Log.d(TAG, "testQuery: ColumnOLang " + COLUMN_ORIGINAL_LANG);

        cursor.moveToFirst();

        int id = cursor.getInt(COLUMN_ID);
        String title = cursor.getString(COLUMN_TITLE);
        String originalLang = cursor.getString(COLUMN_ORIGINAL_LANG);

        assertEquals(id, movie.getId());
        assertEquals(title, movie.getTitle());
        assertEquals(originalLang, movie.getOriginalLanguage());

    }

    public void testDelete() throws Exception{

        ContentResolver contentResolver = mContext.getContentResolver();

        int deletedRows = contentResolver.delete(MoviesTable.CONTENT_URI, MoviesTable.FIELD_ID + "=?", new String[]{String.valueOf(movie.getId())});

        assertEquals(deletedRows, 1);
    }

}
