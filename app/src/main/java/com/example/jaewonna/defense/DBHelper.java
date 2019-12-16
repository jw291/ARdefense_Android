package com.example.jaewonna.defense;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public DBHelper(Context context) {
        super(context, "chat", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("db생성");
        String chatdbsql = "create table chat ("+
                "b_no integer primary key autoincrement,"+
                "chat_key not null,"+
                "user_id not null,"+
                "user_content text not null)";
        db.execSQL(chatdbsql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(newVersion == DATABASE_VERSION){
            System.out.println("drop??");
            db.execSQL("drop table chat");
            onCreate(db);
        }
    }
}
