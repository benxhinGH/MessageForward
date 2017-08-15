package com.usiellau.messageforward.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String CREATE_NUMBER_MONITOR="create table number_monitor(" +
            "id integer primary key autoincrement," +
            "number text)";
    public static final String CREATE_NUMBER_FORWARD="create table number_forward(" +
            "id integer primary key autoincrement," +
            "number text)";
    public static final String CREATE_EMAIL_FORWARD="create table email_forward(" +
            "id integer primary key autoincrement," +
            "address text)";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NUMBER_MONITOR);
        db.execSQL(CREATE_NUMBER_FORWARD);
        db.execSQL(CREATE_EMAIL_FORWARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
