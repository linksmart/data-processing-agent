package org.fraunhofer.fit.almanac.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.fraunhofer.fit.almanac.model.IssueStatus;
import org.fraunhofer.fit.almanac.model.PicIssueUpdate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by devasya on 06.07.2015.
 */
public class IssueStatusDBWrapper {
    private static final String TAG = "IssueStatusDBWrapper";
    FeedReaderDbHelper mDbHelper;
    public  IssueStatusDBWrapper(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }



    public List<IssueStatus> getAllIssues() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_STRING_ENTRY_ID,
                FeedReaderContract.FeedEntry.COLUMN_STRING_NAME,
                FeedReaderContract.FeedEntry.COLUMN_STRING_COMMENT,
                FeedReaderContract.FeedEntry.COLUMN_STRING_PICPATH,
                FeedReaderContract.FeedEntry.COLUMN_INT_PRIORITY,
                FeedReaderContract.FeedEntry.COLUMN_INT_STATE,
                FeedReaderContract.FeedEntry.COLUMN_DATE_TIMETOCOMPLETION,
                FeedReaderContract.FeedEntry.COLUMN_BOOL_SUBSCRIBED,
        };
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_INT_STATE + " ASC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<IssueStatus> issueStatusList = new LinkedList<>();
        if (cursor.moveToFirst()){

            while(!cursor.isAfterLast()){
                IssueStatus issueStatus = new IssueStatus();
                issueStatus.id = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_STRING_ENTRY_ID));
                Log.i(TAG, "Reading row:"+ issueStatus.id);
                issueStatus.name = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_STRING_NAME));
                issueStatus.comment = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_STRING_COMMENT));
                issueStatus.picPath = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_STRING_PICPATH));

                issueStatus.state = PicIssueUpdate.State.fromInteger(cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_INT_STATE)));
                issueStatus.priority = PicIssueUpdate.Priority.fromInteger(cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_INT_PRIORITY)));
                issueStatus.timeToCompletion = new Date(cursor.getLong(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_DATE_TIMETOCOMPLETION)));
                issueStatus.isSubscribed = cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_BOOL_SUBSCRIBED))>0;
                issueStatusList.add(issueStatus);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return issueStatusList;
    }

    public void addIssue(IssueStatus issueStatus){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.i(TAG, "adding row:"+ issueStatus.id);
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_ENTRY_ID, issueStatus.id);
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_NAME, issueStatus.name);
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_COMMENT, issueStatus.comment);
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_PICPATH, issueStatus.picPath);
        if(issueStatus.state != null) {
            values.put(FeedReaderContract.FeedEntry.COLUMN_INT_STATE, issueStatus.state.ordinal());
        }
        if(issueStatus.priority != null)
            values.put(FeedReaderContract.FeedEntry.COLUMN_INT_PRIORITY, issueStatus.priority.ordinal());
        if(issueStatus.timeToCompletion != null)
            values.put(FeedReaderContract.FeedEntry.COLUMN_DATE_TIMETOCOMPLETION, issueStatus.timeToCompletion.getTime());
        values.put(FeedReaderContract.FeedEntry.COLUMN_BOOL_SUBSCRIBED, issueStatus.isSubscribed);

        // Insert the new row, returning the primary key value of the new row
        db.insert(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                null,
                values);
    }

    public void updateIssue(String id,IssueStatus issueStatus){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Log.i(TAG, "Updating a new row");
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_ENTRY_ID, issueStatus.id);
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_NAME, issueStatus.name);
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_COMMENT, issueStatus.comment);
        values.put(FeedReaderContract.FeedEntry.COLUMN_STRING_PICPATH, issueStatus.picPath);
        if(issueStatus.state != null)
            values.put(FeedReaderContract.FeedEntry.COLUMN_INT_STATE, issueStatus.state.ordinal());
        if(issueStatus.priority != null)
            values.put(FeedReaderContract.FeedEntry.COLUMN_INT_PRIORITY, issueStatus.priority.ordinal());
        if(issueStatus.timeToCompletion != null)
            values.put(FeedReaderContract.FeedEntry.COLUMN_DATE_TIMETOCOMPLETION, issueStatus.timeToCompletion.getTime());
        values.put(FeedReaderContract.FeedEntry.COLUMN_BOOL_SUBSCRIBED, issueStatus.isSubscribed);

        // Which row to update, based on the ID
        String selection = FeedReaderContract.FeedEntry.COLUMN_STRING_ENTRY_ID + "=" + id;

        int count = db.update(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                null);
    }

    public void deleteIssue(String id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = FeedReaderContract.FeedEntry.COLUMN_STRING_ENTRY_ID + "="+id;

        // Issue SQL statement.
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, null);
    }
}
