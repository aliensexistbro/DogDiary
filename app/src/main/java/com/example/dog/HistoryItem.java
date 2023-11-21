package com.example.dog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
//Displays the data specified by the History activity
public class HistoryItem extends AppCompatActivity {
    // Shared Preferences
    private static final String PREF_NAME = "DogPrefs";
    private String KEY_NAME = "name";

    // UI elements
    //Names of the Shared Preferences used to store data

    private static final String ACTIVITY_PREF_NAME = "ActivityPrefs";

    //The key for the data that will always be needed in this activity, the name

    //UI variables for photo, text views, buttons and navigation
    private ImageView pictureImageView;
    private TextView logTitleTextView;
    private TextView titlePicTextView;
    private ImageButton historyItemButton;
    private BottomNavigationView appNavigation;

    private Button deleteBtn;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_item);
        logTitleTextView = findViewById(R.id.logTitleTextView);
        titlePicTextView = findViewById(R.id.pictureTitleTextView);
        pictureImageView = findViewById(R.id.pictureImageView);
        historyItemButton = findViewById(R.id.historyItemBackButton);
        deleteBtn = findViewById(R.id.deleteDailyLogButton);
        // Display welcome message and set up navigation
        displayWelcomeMessage();
        setUpNavigation();

        // Back button click listener
        historyItemButton.setOnClickListener(new View.OnClickListener(){
            //Set functionality to go back to the history activity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryItem.this, History.class);
                startActivity(intent);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }
    // Method to delete data based on the selected date
    private void deleteData() {
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        MyDatabase myDatabase = new MyDatabase(this);
        boolean deleted = myDatabase.deleteDataForDate(selectedDate);
        if (deleted) {
            // Row deleted successfully
            Toast.makeText(this, "Entry deleted successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HistoryItem.this, History.class);
            startActivity(intent);
        } else {
            // Failed to delete row
            Toast.makeText(this, "Failed to delete entry", Toast.LENGTH_SHORT).show();
        }
    }

    // Display user activity information
    //Gets the information from the database through iterating through the data of the date specified user and assigning that information to text views.
    private void displayInfo(Cursor cursor1)
    {
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getDataForDate(selectedDate);

        // Display information if data is available
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
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

    // Display activity information and set visibility based on count
    private void displayActivityInfo(int count, String key, int countTextViewId) {
        TextView countTextView = findViewById(countTextViewId);
        countTextView.setText(key + ": " + count);
        countTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    //Method to display the photo if there is one
    private void displayPhoto(String photoPath) {
        pictureImageView.setImageURI(Uri.parse(photoPath));
    }

    //Method that displays the headings of the page like the log date and name of the dog
    //if there is a picture available in the database it will assign the picture to display, if not it will handle it.
    private void displayWelcomeMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        String name = sharedPreferences.getString(KEY_NAME, "");
        logTitleTextView.setText(name + "'s Day " + selectedDate);
        MyDatabase myDatabase = new MyDatabase(this);
        myDatabase.printAllData();
        Cursor cursor = myDatabase.getDataForDate(selectedDate);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            @SuppressLint("Range") String photoPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_PATH));
            if (!photoPath.equals("none")) {
                displayPhoto(photoPath);
                titlePicTextView.setText("Picture of the Day");
            } else {
                titlePicTextView.setText("No Picture of the Day");
            }
            displayInfo(cursor);
        } else {
            titlePicTextView.setText("No Entry");
            pictureImageView.setVisibility(View.GONE);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    // Set up bottom navigation
    private void setUpNavigation() {
        appNavigation = findViewById(R.id.historyItemBottomNavigation);
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem) {
                    intent = new Intent(HistoryItem.this, MainActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.historyMenuItem) {
                    intent = new Intent(HistoryItem.this, History.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.profileMenuItem) {
                    intent = new Intent(HistoryItem.this, DogInfo.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
}
