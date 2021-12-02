package com.example.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    public static final String DB_NAME ="info_users";
    public static final int DB_VERSION=1;
    public static final String TABLE_NAME="USERS";
    public static final String PHONE_NO="PHONE_NO";

    public DBHandler(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+TABLE_NAME+" ("+PHONE_NO+" TEXT)";
        db.execSQL(query);
    }

    public void addNewRecord(String phone)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(PHONE_NO,phone);
        db.insert(TABLE_NAME,null,values);
        System.out.println("Records added..");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
