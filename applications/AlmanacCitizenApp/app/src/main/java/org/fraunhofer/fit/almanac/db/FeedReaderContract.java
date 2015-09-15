package org.fraunhofer.fit.almanac.db;

import android.provider.BaseColumns;

/**
 * Created by devasya on 06.07.2015.
 */
public class FeedReaderContract{
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "IssueStatus";
        public static final String COLUMN_STRING_ENTRY_ID = "id";
        public static final String COLUMN_STRING_NAME = "name";
        public static final String COLUMN_STRING_COMMENT = "comment";
        public static final String COLUMN_STRING_PICPATH = "picPath";
        public static final String COLUMN_INT_PRIORITY = "priority";
        public static final String COLUMN_INT_STATE = "state";
        public static final String COLUMN_DATE_TIMETOCOMPLETION = "timeToCompletion";
        public static final String COLUMN_BOOL_SUBSCRIBED = "isSubscribed";
        public static final String COLUMN_DATE_CREATIONDATE = "creationDate";
        public static final String COLUMN_DATE_UPDATEDATE = "updateDate";
        public static final String COLUMN_BOOL_ISUPDATED = "updated";
    }
}