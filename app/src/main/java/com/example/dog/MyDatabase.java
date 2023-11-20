package com.example.dog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase(Context c) {
        context = c;
        helper = new MyHelper(context);
    }

    public long insertPhotoData(String photoPath, String timestamp) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.PHOTO_PATH, photoPath);
        contentValues.put(Constants.TIMESTAMP, timestamp);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getPhotoData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.PHOTO_PATH, Constants.TIMESTAMP};
        return db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
    }

    public Cursor getPhotoDataForToday() {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Get today's date in the format "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDateString = dateFormat.format(new Date());

        // Define the selection and selectionArgs for the WHERE clause
        String selection = "SUBSTR(" + Constants.TIMESTAMP + ", 1, 10) = ?";
        String[] selectionArgs = { todayDateString };

        String[] columns = {Constants.PHOTO_PATH, Constants.TIMESTAMP};

        // Query the database with the WHERE clause
        return db.query(Constants.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }


}

