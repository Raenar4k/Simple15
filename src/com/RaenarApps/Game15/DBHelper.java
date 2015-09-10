package com.RaenarApps.Game15;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Raenar on 04.09.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DB_NAME = "listDB.db";
    public static final String TABLE_NAME = "listTable";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Image.TITLE + " TEXT, "
                + Image.IMAGE_PATH + " TEXT, " + Image.THUMBNAIL_PATH + " TEXT, "
                + Image.IS_DEFAULT + " INTEGER, " + Image.IS_PROCESSED + " INTEGER, "
                + Image.DOMINANT_COLOR + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
