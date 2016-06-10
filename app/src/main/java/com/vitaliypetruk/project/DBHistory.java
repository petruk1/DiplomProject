package com.vitaliypetruk.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vital on 09.06.16.
 */
public class DBHistory extends SQLiteOpenHelper{
    public DBHistory(Context context) {
        super(context,"History",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table messageHistory ("
                + "convid integer autoincrement,"
                + "fromjid text,"
                + "tojid text," +
                "message text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
