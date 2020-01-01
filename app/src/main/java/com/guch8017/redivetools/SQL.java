package com.guch8017.redivetools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQL extends SQLiteOpenHelper {
    static final private String db_name = "data.db";

    public SQL(Context context){
        super(context, context.getFilesDir().getPath()+'/'+db_name, null, 1);
        String path = context.getFilesDir().getPath();
        Log.w("Database",path);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "create table DATA(" +
                     "[ID] integer PRIMARY KEY autoincrement," +
                     "[M3F1YSNkOnF0] string NOT NULL," +
                     "[MHx5cg%3D%3D] string NOT NULL," +
                     "[NnB%2FZDJpMHx5cg%3D%3D] string NOT NULL," +
                     "[Description] string," +
                     "[Server] int NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
