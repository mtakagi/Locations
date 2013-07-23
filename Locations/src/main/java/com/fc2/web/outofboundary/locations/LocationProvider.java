package com.fc2.web.outofboundary.locations;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by mtakagi on 13/07/21.
 */
public class LocationProvider extends ContentProvider {

    public static final String AUTHORITY = "com.fc2.web.outofboundary.locations.provider";

    public static final Uri ACTIVITY_URI = Uri.parse("content://" + AUTHORITY + "/activity");

    private static final UriMatcher sMather = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ACTIVITY = 1;
    private static final int ACTIVITY_ID = 2;

    static {
        sMather.addURI(AUTHORITY, "activity", ACTIVITY);
        sMather.addURI(AUTHORITY, "activity/#", ACTIVITY_ID);
    }

    private LocationDBOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new LocationDBOpenHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        if (sMather.match(uri) == ACTIVITY) {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

            builder.setTables(LocationDBOpenHelper.ACTIVITY_TABLE);

            Cursor cursor = builder.query(db, strings, s, null, null, null, s2);

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (sMather.match(uri)) {
            case ACTIVITY:
                return "vnd.android.cursor.dir/vnd.outofboundary.activity";
            case ACTIVITY_ID:
                return "vnd.android.cursor.item/vnd.outofboundary.activity";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (sMather.match(uri) == ACTIVITY) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = db.insert(LocationDBOpenHelper.ACTIVITY_TABLE, null, contentValues);
            Uri result = ContentUris.withAppendedId(uri, id);

            getContext().getContentResolver().notifyChange(result, null);

            return result;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        if (sMather.match(uri) == ACTIVITY_ID) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            int count = db.delete(LocationDBOpenHelper.ACTIVITY_TABLE, s, strings);

            getContext().getContentResolver().notifyChange(uri, null);

            return count;
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if (sMather.match(uri) == ACTIVITY_ID) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            int count = db.update(LocationDBOpenHelper.ACTIVITY_TABLE, contentValues, s, strings);

            getContext().getContentResolver().notifyChange(uri, null);

            return count;
        }
        return 0;
    }

    private static class LocationDBOpenHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "location.db";
        private static final String ACTIVITY_TABLE = "activity";
        private static final int VERSION = 1;
        private static final String CREATE_ACTIVITY_TABLE =
                "CREATE TABLE IF NOT EXISTS " + ACTIVITY_TABLE + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "type INTEGER NOT NULL," +
                        "confidence INTEGER NOT NULL," +
                        "time INTEGER NOT NULL);";

        public LocationDBOpenHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_ACTIVITY_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}
