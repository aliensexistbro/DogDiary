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

import com.example.dog.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "DogPrefs";

    private static final String ACTIVITY_PREF_NAME = "ActivityPrefs";
    private String KEY_NAME = "name";

    private Button cameraButton, logBtn;
    private ImageButton historyBtn;
    private ImageView pictureImageView;

    private TextView logTitleTextView;
    private TextView titlePicTextView;

    BottomNavigationView appNavigation;


    @SuppressLint({"Range", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logTitleTextView = findViewById(R.id.logTitleTextView);
        cameraButton = findViewById(R.id.cameraButton);
        titlePicTextView = findViewById(R.id.pictureTitleTextView);
        pictureImageView = findViewById(R.id.pictureImageView);
        logBtn = findViewById(R.id.logButton);
        historyBtn = findViewById(R.id.historyButton);

        historyBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Move to DogInfo activity
            Intent intent = new Intent(MainActivity.this, History.class);
            startActivity(intent);
            finish(); // Finish current activity to prevent going back to it with the back button
        }
        });

        
        appNavigation = findViewById(R.id.homeBottomNavigation);
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem){
                }
                if(item.getItemId() == R.id.historyMenuItem){
                    intent = new Intent(MainActivity.this, History.class);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.profileMenuItem){
                }
                return true;
            }
        });

        if (!hasDogInfo()) {
            displayWelcomePage();
        } else {
            displayWelcomeMessage();
        }


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
                if(hasActivityInfo()) {
                    displayActivityInfo(poopCount, Constants.POOP_COUNT, R.id.poopCountTextView, R.id.poopDateTextView);
                    displayActivityInfo(peeCount, Constants.PEE_COUNT, R.id.peeCountTextView, R.id.peeDateTextView);
                    displayActivityInfo(foodCount, Constants.FOOD_COUNT, R.id.foodCountTextView, R.id.foodDateTextView);
                }
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

    private boolean hasDogInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Check if any key exists in SharedPreferences (indicating dog information)
        return sharedPreferences.getAll().size() > 0;
    }

    private boolean hasActivityInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(ACTIVITY_PREF_NAME, Context.MODE_PRIVATE);
        // Check if any key exists in SharedPreferences (indicating dog information)
        return sharedPreferences.getAll().size() > 0;
    }

    private void displayWelcomePage() {
        setContentView(R.layout.activity_welcome);

        Button nextButton = findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to DogInfo activity
                Intent intent = new Intent(MainActivity.this, DogInfo.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });
    }

    private void displayWelcomeMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // Display name
        String name = sharedPreferences.getString(KEY_NAME, "");
        logTitleTextView.setText(name + "'s Day");
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to DogInfo activity
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getPhotoDataForToday();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            @SuppressLint("Range") String photoPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_PATH));
            if(!photoPath.equals("none")){
                Log.d("photo", photoPath);
                displayPhoto(photoPath);
                titlePicTextView.setText("Picture of the Day");
            } else
            {
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
                // Move to DogInfo activity
                Intent intent = new Intent(MainActivity.this, ActivityLog.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to DogInfo activity
                Intent intent = new Intent(MainActivity.this, History.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });
        displayInfo();
    }
}

