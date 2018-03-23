package com.dwman.log.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ldw on 2018/3/19.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dms.db";
    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String logSql ="create table allLog(_id integer primary key autoincrement,type varchar(2)," +
                "action varchar(2),date integer,content text)";
        String movieSql = "create table movieLog(_id integer primary key autoincrement,action varchar(2)" +
                ",date integer,movieId integer,name varchar(20),content text)";
        String adSql = "create table adLog(_id integer primary key autoincrement,action varchar(2)" +
                ",date integer,adId integer,name varchar(20),content text)";
        sqLiteDatabase.execSQL(logSql);
        sqLiteDatabase.execSQL(movieSql);
        sqLiteDatabase.execSQL(adSql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
