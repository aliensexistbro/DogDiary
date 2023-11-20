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

    public long insertPhotoData(String photoPath, String timestamp, int poop_count, int pee_count, int food_count, int walk_count) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.PHOTO_PATH, photoPath);
        contentValues.put(Constants.TIMESTAMP, timestamp);
        contentValues.put(Constants.POOP_COUNT, poop_count);
        contentValues.put(Constants.PEE_COUNT, pee_count);
        contentValues.put(Constants.FOOD_COUNT, food_count);
        contentValues.put(Constants.WALK_COUNT, walk_count);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
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

    public Cursor getColumnDataForToday() {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Get today's date in the format "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDateString = dateFormat.format(new Date());

        // Define the selection and selectionArgs for the WHERE clause
        String selection = "SUBSTR(" + Constants.TIMESTAMP + ", 1, 10) = ?";
        String[] selectionArgs = { todayDateString };

        // Include all columns in the table
        String[] columns = {
                Constants.UID,
                Constants.PHOTO_PATH,
                Constants.TIMESTAMP,
                Constants.POOP_COUNT,
                Constants.PEE_COUNT,
                Constants.FOOD_COUNT,
                Constants.WALK_COUNT
                // Add other column names as needed
        };

        // Query the database with the WHERE clause
        return db.query(Constants.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    public int updateData(String Constant, String date, String newValue) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant, newValue);

        // Specify the WHERE clause based on the old timestamp
        String whereClause = Constants.TIMESTAMP + "=?";
        String[] whereArgs = {date};

        // Perform the update
        int rowsUpdated = db.update(Constants.TABLE_NAME, contentValues, whereClause, whereArgs);

        return rowsUpdated;
    }

    public int updateIntData(String Constant, String date,int newValue) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant, newValue);

        // Specify the WHERE clause based on the old timestamp
        String whereClause = Constants.TIMESTAMP + "=?";
        String[] whereArgs = {date};

        // Perform the update
        int rowsUpdated = db.update(Constants.TABLE_NAME, contentValues, whereClause, whereArgs);

        return rowsUpdated;
    }
}

