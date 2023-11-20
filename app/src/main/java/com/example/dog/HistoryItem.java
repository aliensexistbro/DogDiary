package com.example.dog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryItem extends AppCompatActivity {

    private static final String PREF_NAME = "DogPrefs";

    private static final String ACTIVITY_PREF_NAME = "ActivityPrefs";
    private String KEY_NAME = "name";
    private ImageView pictureImageView;

    private TextView logTitleTextView;
    private TextView titlePicTextView;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_item);
        logTitleTextView = findViewById(R.id.logTitleTextView);
        titlePicTextView = findViewById(R.id.pictureTitleTextView);
        pictureImageView = findViewById(R.id.pictureImageView);
        displayWelcomeMessage();
    }

    private void displayInfo()
    {
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getColumnDataForToday();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Assuming you have a method to display photos, replace the following line accordingly
                cursor.moveToFirst();
                @SuppressLint("Range") int poopCount = cursor.getInt(cursor.getColumnIndex(Constants.POOP_COUNT));
                @SuppressLint("Range") int peeCount = cursor.getInt(cursor.getColumnIndex(Constants.PEE_COUNT));
                @SuppressLint("Range") int foodCount = cursor.getInt(cursor.getColumnIndex(Constants.FOOD_COUNT));
                @SuppressLint("Range") int walkCount = cursor.getInt(cursor.getColumnIndex(Constants.WALK_COUNT));
                @SuppressLint("Range") int walkStep = cursor.getInt(cursor.getColumnIndex(Constants.WALK_STEP_COUNT));
                @SuppressLint("Range") int walkTime = cursor.getInt(cursor.getColumnIndex(Constants.WALK_TIME));
                // Display the counts in TextViews and set visibility
                displayActivityInfo(poopCount, Constants.POOP_COUNT, R.id.poopCountTextView);
                displayActivityInfo(peeCount, Constants.PEE_COUNT, R.id.peeCountTextView);
                displayActivityInfo(foodCount, Constants.FOOD_COUNT, R.id.foodCountTextView);
                displayActivityInfo(walkCount, Constants.WALK_COUNT, R.id.walkCountTextView);
                displayActivityInfo(walkStep, Constants.WALK_STEP_COUNT, R.id.stepsTextView);
                displayActivityInfo(walkTime, Constants.WALK_TIME, R.id.walkTimeTextView);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

    }

    private void displayActivityInfo(int count, String key, int countTextViewId, int dateTextViewId) {
        TextView countTextView = findViewById(countTextViewId);
        TextView dateTextView = findViewById(dateTextViewId);

        SharedPreferences sharedPreferences = getSharedPreferences(ACTIVITY_PREF_NAME, MODE_PRIVATE);
        String storedCount = sharedPreferences.getString(key, "");

        if (storedCount != null && !storedCount.equals("")) {
            dateTextView.setText("Last Time: " + storedCount);
            dateTextView.setVisibility(View.VISIBLE);
            Log.d("Stored ", key + storedCount);
        } else {
            dateTextView.setVisibility(View.GONE);
        }

        countTextView.setText(key + ": " + count);
        // Set visibility based on count
        countTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    private void displayActivityInfo(int count, String key, int countTextViewId) {
        TextView countTextView = findViewById(countTextViewId);
        // Set visibility based on count
        countTextView.setText(key + ": " + count);
        countTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    private void displayPhoto(String photoPath) {
        pictureImageView.setImageURI(Uri.parse(photoPath));
    }


    private boolean hasActivityInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(ACTIVITY_PREF_NAME, Context.MODE_PRIVATE);
        // Check if any key exists in SharedPreferences (indicating dog information)
        return sharedPreferences.getAll().size() > 0;
    }


    private void displayWelcomeMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // Display name
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        String name = sharedPreferences.getString(KEY_NAME, "");
        logTitleTextView.setText(name + "'s Day " + selectedDate);
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getPhotoDataForDate(selectedDate);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            @SuppressLint("Range") String photoPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_PATH));
            if(!photoPath.equals("none")){
                Log.d("photo", photoPath);
                displayPhoto(photoPath);
                titlePicTextView.setText("Picture of the Day");
            } else
            {
                titlePicTextView.setText("No Picture of the Day");
            }
            displayInfo();
        } else {
            // Handle case where there is no data for today
            titlePicTextView.setText("No Entry");
            pictureImageView.setVisibility(View.GONE);
        }
        // Close the cursor to free up resources
        if (cursor != null) {
            cursor.close();
        }
    }
}