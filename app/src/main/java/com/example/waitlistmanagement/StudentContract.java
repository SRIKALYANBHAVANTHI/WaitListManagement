package com.example.waitlistmanagement;

import android.provider.BaseColumns;

public final class StudentContract {
    private StudentContract() {}

    public static class StudentEntry implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COURSE = "course";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StudentEntry.TABLE_NAME + " (" +
                    StudentEntry._ID + " INTEGER PRIMARY KEY," +
                    StudentEntry.COLUMN_NAME_NAME + " TEXT," +
                    StudentEntry.COLUMN_NAME_COURSE + " TEXT," +
                    StudentEntry.COLUMN_NAME_PRIORITY + " TEXT)";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + StudentEntry.TABLE_NAME;
}
