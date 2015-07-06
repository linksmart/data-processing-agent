package org.fraunhofer.fit.almanac.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.fraunhofer.fit.almanac.db.FeedReaderContract.FeedEntry;
/**
 * Created by devasya on 06.07.2015.
 */
public class FeedReaderDbHelper  extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String INTEGER_TYPE = " INTEGER";


    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_STRING_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_STRING_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_STRING_COMMENT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_STRING_PICPATH + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INT_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INT_STATE + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_DATE_TIMETOCOMPLETION + NUMERIC_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_BOOL_SUBSCRIBED + NUMERIC_TYPE +
          " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}