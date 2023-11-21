package com.example.dog;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyHelper extends SQLiteOpenHelper {

    private Context context;

    // SQL command to create the database table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + Constants.TABLE_NAME + " (" +
                    Constants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.PHOTO_PATH + " TEXT, " +
                    Constants.TIMESTAMP + " TEXT, " +
                    Constants.POOP_COUNT + " INTEGER, " +
                    Constants.PEE_COUNT + " INTEGER, " +
                    Constants.FOOD_COUNT + " INTEGER , " +
                    Constants.WALK_COUNT + " INTEGER, " +
                    Constants.WALK_STEP_COUNT + " INTEGER , " +
                    Constants.WALK_TIME + " INTEGER);";

    // SQL command to drop the database table if it exists
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME;

    // Constructor to initialize the context and database details
    public MyHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    // Database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Executing the SQL command to create the table
            db.execSQL(CREATE_TABLE);
            Toast.makeText(context, "onCreate() called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "Exception in onCreate() db", Toast.LENGTH_LONG).show();
        }
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Execute the SQL command to drop the existing table and recreate it
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e) {
            Toast.makeText(context, "Exception in onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }
}
