package com.example.msk.mapsample.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PostDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "posts.db";
    private static final int DATABASE_VERSION = 1;

    /* table and column name */
    public static final String TABLE_NAME           = "posts";
    public static final String COLUMN_ID            = "postId";
    public static final String COLUMN_COMMENT       = "comment";
    public static final String COLUMN_IMAGE         = "image";
    public static final String COLUMN_LOCATION      = "location";
    public static final String COLUMN_POST_DATE     = "postDate";
    public static final String COLUMN_UPDATE_DATE   = "updateDate";

    /* SQL statement for creating table */
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COMMENT      + " TEXT, " +
                    COLUMN_IMAGE        + " TEXT, " +
                    COLUMN_LOCATION     + " TEXT, " +
                    COLUMN_POST_DATE    + " NUMERIC, " +
                    COLUMN_UPDATE_DATE  + " NUMERIC " +
                    ")";

    public PostDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(TABLE_CREATE);
    }
}
