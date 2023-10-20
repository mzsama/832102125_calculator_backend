package com.example.calculator_plus;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MySqliteHelper extends SQLiteOpenHelper {
    public static int num=0;
    private static final String DATABASE_NAME = "CalculatorResults.db";
    private static final int DATABASE_VERSION = 1;

    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public String recur(String temp){
        List<String>resultList=getRecentResults();
        if (resultList.size() > 1) {
            String prevResult = resultList.get(1);
            return prevResult;
        } else {
            return "NULL";
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the calculations table
        String CREATE_RESULT_TABLE = "CREATE TABLE IF NOT EXISTS calculations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "result TEXT)";
        db.execSQL(CREATE_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade policy when database schema changes
    }

    // Insert a result into the database
    public void insertResult(String result) {
        num++;
        ContentValues values = new ContentValues();
        values.put("result", result);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("calculations", null, values);
        db.close();

    }
    public void deleteOne(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("calculations","id=?",new String[]{"1"});
    }
    public void deleteAllResults() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("calculations", null, null);
        db.close();
    }

    @SuppressLint("Range")
    public List<String> getRecentResults() {
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "calculations",
                new String[]{"result"},
                null,
                null,
                null,
                null,
                "id DESC",
                "10"
        );
        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(cursor.getColumnIndex("result")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return results;
    }
}
