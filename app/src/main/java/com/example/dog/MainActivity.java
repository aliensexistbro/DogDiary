package com.example.dog;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
public class MainActivity extends AppCompatActivity {

    // Shared preferences file names
    private static final String PREF_NAME = "DogPrefs";
    private static final String ACTIVITY_PREF_NAME = "ActivityPrefs";

    // Key for storing dog's name in shared preferences
    private String KEY_NAME = "name";

    // UI elements
    private Button cameraButton, logBtn;
    private ImageView pictureImageView;
    private TextView logTitleTextView;
    private TextView titlePicTextView;
    private BottomNavigationView appNavigation;

    @SuppressLint({"Range", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        logTitleTextView = findViewById(R.id.logTitleTextView);
        cameraButton = findViewById(R.id.cameraButton);
        titlePicTextView = findViewById(R.id.pictureTitleTextView);
        pictureImageView = findViewById(R.id.pictureImageView);
        logBtn = findViewById(R.id.logButton);

        // Set up bottom navigation
        appNavigation = findViewById(R.id.homeBottomNavigation);
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem) {
                    // Already home
                }
                if (item.getItemId() == R.id.historyMenuItem) {
                    // Move to History activity
                    intent = new Intent(MainActivity.this, History.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.profileMenuItem) {
                    // Move to DogInfo activity
                    intent = new Intent(MainActivity.this, DogProfile.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        // Check if dog information exists
        if (!hasDogInfo()) {
            // Display welcome page
            displayWelcomePage();
        } else {
            // Display welcome message and dog's information
            displayWelcomeMessage();
        }
    }

    // Check if dog information exists in shared preferences
    private boolean hasDogInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Check if any key exists in SharedPreferences (indicating dog information)
        return sharedPreferences.getAll().size() > 0;
    }

    // Check if activity information exists in shared preferences
    private boolean hasActivityInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(ACTIVITY_PREF_NAME, Context.MODE_PRIVATE);
        // Check if any key exists in SharedPreferences (indicating activity information)
        return sharedPreferences.getAll().size() > 0;
    }

    // Display dog's information
    @SuppressLint("Range")
    private void displayInfo() {
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getColumnDataForToday();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Display various activity counts and update visibility
                displayActivityInfo(cursor.getInt(cursor.getColumnIndex(Constants.POOP_COUNT)),
                        Constants.POOP_COUNT, R.id.poopCountTextView, R.id.poopDateTextView);
                displayActivityInfo(cursor.getInt(cursor.getColumnIndex(Constants.PEE_COUNT)),
                        Constants.PEE_COUNT, R.id.peeCountTextView, R.id.peeDateTextView);
                displayActivityInfo(cursor.getInt(cursor.getColumnIndex(Constants.FOOD_COUNT)),
                        Constants.FOOD_COUNT, R.id.foodCountTextView, R.id.foodDateTextView);
                displayActivityInfo(cursor.getInt(cursor.getColumnIndex(Constants.WALK_COUNT)),
                        Constants.WALK_COUNT, R.id.walkCountTextView, R.id.lastWalkTimeTextView);
                displayActivityInfo(cursor.getInt(cursor.getColumnIndex(Constants.WALK_STEP_COUNT)),
                        Constants.WALK_STEP_COUNT, R.id.stepsTextView);
                displayActivityInfo(cursor.getInt(cursor.getColumnIndex(Constants.WALK_TIME)),
                        Constants.WALK_TIME, R.id.walkTimeTextView);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    // Display activity information with date if available
    private void displayActivityInfo(int count, String key, int countTextViewId, int dateTextViewId) {
        TextView countTextView = findViewById(countTextViewId);
        TextView dateTextView = findViewById(dateTextViewId);

        SharedPreferences sharedPreferences = getSharedPreferences(ACTIVITY_PREF_NAME, MODE_PRIVATE);
        String storedCount = sharedPreferences.getString(key, "");

        if (count > 0 && storedCount != null && !storedCount.equals("")) {
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

    // Display activity information without date
    private void displayActivityInfo(int count, String key, int countTextViewId) {
        TextView countTextView = findViewById(countTextViewId);
        // Set visibility based on count
        countTextView.setText(key + ": " + count);
        countTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    // Display the photo using the provided photoPath
    private void displayPhoto(String photoPath) {
        pictureImageView.setImageURI(Uri.parse(photoPath));
    }

    // Display the welcome page prompting user to provide dog information
    private void displayWelcomePage() {
        setContentView(R.layout.activity_welcome);

        Button nextButton = findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to DogInfo activity
                Intent intent = new Intent(MainActivity.this, DogInfo.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Display the welcome message and information for an existing dog
    private void displayWelcomeMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // Dog's name
        String name = sharedPreferences.getString(KEY_NAME, "");
        logTitleTextView.setText(name + "'s Day");
        // Set up click listener for camera button
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to CameraActivity
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });

        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getPhotoDataForToday();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            // Get photo path from the cursor
            @SuppressLint("Range") String photoPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_PATH));
            if (!photoPath.equals("none")) {
                // Display the photo
                Log.d("photo", photoPath);
                displayPhoto(photoPath);
                titlePicTextView.setText("Picture of the Day");
            } else {
                // Set text if there is no photo for the day
                titlePicTextView.setText("Take a Picture of the Day!");
            }
        } else {
            // Handle case where there is no data for today
            titlePicTextView.setText("Take a Picture of the Day!");
        }
        // Close the cursor to free up resources
        if (cursor != null) {
            cursor.close();
        }
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to ActivityLog activity
                Intent intent = new Intent(MainActivity.this, ActivityLog.class);
                startActivity(intent);
                finish();
            }
        });

        // Display additional information
        displayInfo();
    }
}

