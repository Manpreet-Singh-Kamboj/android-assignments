package com.manpreet.androidassignments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChatDatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "ChatDB";
    static final int VERSION_NUM = 3;
    static final String HELPER_NAME = "ChatDatabaseHelper";

    // Table Names
    static final String TABLE_NAME = "messages";

    // Column Names
    static final String KEY_ID = "id";
    static final String KEY_MESSAGE = "message";

    // Queries
    static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE + " TEXT)";
     public ChatDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null, VERSION_NUM);
     }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(HELPER_NAME, "Calling onCreate");
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        Log.i(HELPER_NAME, "Calling onUpgrade, oldVersion=" + oldVer + " newVersion=" + newVer);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
