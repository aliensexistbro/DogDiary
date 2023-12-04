package com.example.dog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;   
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityLog extends AppCompatActivity {
    private ImageButton walkBtn, foodBtn, poopBtn, peeBtn;
     private static final String PREF_NAME = "ActivityPrefs";
    private BottomNavigationView appNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        walkBtn = findViewById(R.id.walkBtn);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to Walk activity
                Intent intent = new Intent(ActivityLog.this, Walk.class);
                startActivity(intent);
                finish(); // Finish current activity
            }
        });
        poopBtn = findViewById(R.id.poopBtn);
        poopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log poop data, move to Main activity
                saveToDatabase(Constants.POOP_COUNT);
                Intent intent = new Intent(ActivityLog.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish current activity
            }
        });
        peeBtn = findViewById(R.id.peeBtn);
        peeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log pee data, move to Main activity
                saveToDatabase(Constants.PEE_COUNT);
                Intent intent = new Intent(ActivityLog.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });

        foodBtn = findViewById(R.id.foodBtn);
        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log food data, move to Main activity
                saveToDatabase(Constants.FOOD_COUNT);
                Intent intent = new Intent(ActivityLog.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish current activity
            }
        });

        // App navigation
        appNavigation = findViewById(R.id.logBottomNavigation);
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem){
                    intent = new Intent(ActivityLog.this, MainActivity.class);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.historyMenuItem){
                    intent = new Intent(ActivityLog.this, History.class);
                    startActivity(intent);

                }
                if(item.getItemId() == R.id.profileMenuItem){
                }
                return true;
            }
        });
    }
    private void saveToDatabase(String constant) {
        // Method to save to database
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getColumnDataForToday();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                cursor.moveToFirst();
                @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex(constant)) +1;
                myDatabase.updateIntData(constant, getCurrentDate(), count);
                saveDogInfo(constant, getCurrentTime());
            }
        } else {
            if (constant.equals(Constants.POOP_COUNT)) {
                long id = myDatabase.insertPhotoData("none", getCurrentDate(), 1, 0, 0, 0,0,0);
            } else if (constant.equals(Constants.PEE_COUNT)) {
                long id = myDatabase.insertPhotoData("none", getCurrentDate(), 0, 1, 0, 0, 0,0);
            } else if (constant.equals(Constants.FOOD_COUNT)) {
                long id = myDatabase.insertPhotoData("none", getCurrentDate(), 0, 0, 1, 0,0,0);
            }
            saveDogInfo(constant, getCurrentTime());
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    public static String getCurrentDate() { // Getting today's date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    public static String getCurrentTime() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        // Format the time as a string
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    private void saveDogInfo(String constant, String time) { // Saving last time info to shared pref
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constant, time);
        editor.apply();
    }
}
