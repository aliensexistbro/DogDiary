package com.example.dog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    // Constructor to initialize the context and the database helper
    public MyDatabase(Context c) {
        context = c;
        helper = new MyHelper(context);
    }
    public Cursor getAllData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        // Include all columns in the table
        String[] columns = {
                Constants.UID,
                Constants.PHOTO_PATH,
                Constants.TIMESTAMP,
                Constants.POOP_COUNT,
                Constants.PEE_COUNT,
                Constants.FOOD_COUNT,
                Constants.WALK_COUNT,
                Constants.WALK_STEP_COUNT,
                Constants.WALK_TIME
        };
        // Query the database without any conditions
        return db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
    }

    public void printAllData() {
        Cursor cursor = getAllData();

        try {
            // Iterate through the set and print each item
            while (cursor.moveToNext()) {
                long uid = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.UID));
                String photoPath = cursor.getString(cursor.getColumnIndexOrThrow(Constants.PHOTO_PATH));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(Constants.TIMESTAMP));
                int poopCount = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.POOP_COUNT));
                int peeCount = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.PEE_COUNT));
                int foodCount = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.FOOD_COUNT));
                int walkCount = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.WALK_COUNT));
                int walkStepCount = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.WALK_STEP_COUNT));
                int walkTime = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.WALK_TIME));

                // Print the item details
                Log.d("Database", "UID: " + uid +
                        ", PhotoPath: " + photoPath +
                        ", Timestamp: " + timestamp +
                        ", Poop Count: " + poopCount +
                        ", Pee Count: " + peeCount +
                        ", Food Count: " + foodCount +
                        ", Walk Count: " + walkCount +
                        ", Walk Step Count: " + walkStepCount +
                        ", Walk Time: " + walkTime);
            }
        } finally {
            // Close the cursor to release resources
            cursor.close();
        }
    }

    // Insert a new row in the database
    public long insertPhotoData(String photoPath, String timestamp, int poop_count, int pee_count, int food_count, int walk_count,
                                int step_count, int time_count) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.PHOTO_PATH, photoPath);
        contentValues.put(Constants.TIMESTAMP, timestamp);
        contentValues.put(Constants.POOP_COUNT, poop_count);
        contentValues.put(Constants.PEE_COUNT, pee_count);
        contentValues.put(Constants.FOOD_COUNT, food_count);
        contentValues.put(Constants.WALK_COUNT, walk_count);
        contentValues.put(Constants.WALK_STEP_COUNT, step_count);
        contentValues.put(Constants.WALK_TIME, time_count);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }
    // Retrieve photo data for today
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

    // Retrieve data for a specific date
    public Cursor getDataForDate(String date) {
        SQLiteDatabase db = helper.getWritableDatabase();
        // Define the selection and selectionArgs for the WHERE clause
        String selection = "SUBSTR(" + Constants.TIMESTAMP + ", 1, 10) = ?";
        String[] selectionArgs = { date };
        // All columns in the table
        String[] columns = {
                Constants.UID,
                Constants.PHOTO_PATH,
                Constants.TIMESTAMP,
                Constants.POOP_COUNT,
                Constants.PEE_COUNT,
                Constants.FOOD_COUNT,
                Constants.WALK_COUNT,
                Constants.WALK_STEP_COUNT,
                Constants.WALK_TIME
        };
        // Query the database with the WHERE clause
        return db.query(Constants.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    // Retrieve data for today
    public Cursor getColumnDataForToday() {
        SQLiteDatabase db = helper.getWritableDatabase();
        // Get today's date in the format "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDateString = dateFormat.format(new Date());
        // SelectionArgs for the WHERE clause
        String selection = "SUBSTR(" + Constants.TIMESTAMP + ", 1, 10) = ?";
        String[] selectionArgs = { todayDateString };
        // All columns in the table
        String[] columns = {
                Constants.UID,
                Constants.PHOTO_PATH,
                Constants.TIMESTAMP,
                Constants.POOP_COUNT,
                Constants.PEE_COUNT,
                Constants.FOOD_COUNT,
                Constants.WALK_COUNT,
                Constants.WALK_STEP_COUNT,
                Constants.WALK_TIME
        };

        // Query the database with the WHERE clause
        return db.query(Constants.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    // Update string data in the database
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

    // Update integer data in the database
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

    // Delete data for a specific date
    public boolean deleteDataForDate(String date) {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Specify the WHERE clause based on the timestamp
        String whereClause = "SUBSTR(" + Constants.TIMESTAMP + ", 1, 10) = ?";
        String[] whereArgs = { date };

        // Perform the delete operation
        int rowsDeleted = db.delete(Constants.TABLE_NAME, whereClause, whereArgs);

        // Check if any rows were deleted
        return rowsDeleted > 0;
    }

    public String getDataFromColumn(String columnName){
        SQLiteDatabase db = helper.getWritableDatabase();
        int inquiryIndex = 0;
        String selection = "";
        // All columns in the table
        String[] columns = {
                Constants.UID,
                Constants.PHOTO_PATH,
                Constants.TIMESTAMP,
                Constants.POOP_COUNT,
                Constants.PEE_COUNT,
                Constants.FOOD_COUNT,
                Constants.WALK_COUNT,
                Constants.WALK_STEP_COUNT,
                Constants.WALK_TIME
        };
        switch (columnName){
            case "pee":
                selection = Constants.PEE_COUNT;
                break;
            case "poo":
                selection = Constants.POOP_COUNT;
                break;
            case "walk":
                selection = Constants.WALK_COUNT;
                break;
            case "step":
                selection = Constants.WALK_STEP_COUNT;
                break;
            default:
                selection = Constants.TIMESTAMP;
        }

        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()){
            int timeIndex = cursor.getColumnIndex(Constants.TIMESTAMP);
            int selectionIndex = cursor.getColumnIndex(selection);
            String time = cursor.getString(timeIndex);
            String selectData = cursor.getString(selectionIndex);
            buffer.append(time + " " + selectData + "\n");

        }

        return buffer.toString();
    }



}

