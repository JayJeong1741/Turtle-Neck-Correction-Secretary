package com.example.ui.ui.stretchmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoStretch.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TODO = "todo_stretches";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STRETCH_NAME = "stretch_name";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TODO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STRETCH_NAME + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public long insertTodoStretch(String stretchName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STRETCH_NAME, stretchName);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        return db.insert(TABLE_TODO, null, values);
    }

    public void deleteOldEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        db.delete(TABLE_TODO, COLUMN_TIMESTAMP + " < ?",
                new String[]{String.valueOf(twentyFourHoursAgo)});
    }

    public void deleteStretch(String stretchName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_STRETCH_NAME + " = ?", new String[]{stretchName});
    }

    // 새로 추가된 메서드들
    public void clearAllTodoStretches() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, null, null);
    }

    public boolean isTodoStretchExists(String stretchName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"stretch_name"};
        String selection = "stretch_name = ?";
        String[] selectionArgs = {stretchName};

        Cursor cursor = db.query(TABLE_TODO, projection, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<String> getTodoStretches() {
        SQLiteDatabase db = this.getReadableDatabase();
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        String[] projection = {"stretch_name"};
        String selection = "timestamp >= ?";
        String[] selectionArgs = {String.valueOf(twentyFourHoursAgo)};

        Cursor cursor = db.query(TABLE_TODO, projection, selection, selectionArgs, null, null, null);

        List<String> stretches = new ArrayList<>();
        while (cursor.moveToNext()) {
            stretches.add(cursor.getString(cursor.getColumnIndexOrThrow("stretch_name")));
        }
        cursor.close();

        return stretches;
    }

}