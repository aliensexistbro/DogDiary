package com.example.dog;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Activity that facilitates the viewing of the data stored in the SQL Database
public class History extends AppCompatActivity {
    //View for Calendar
    private CalendarView calendarView;
    //View for bottom navigation
    private BottomNavigationView appNavigation;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        calendarView = findViewById(R.id.calendarView);

        // Set the listener for date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Handle the selected date change
                String formattedMonth = String.format("%02d", month + 1); // Add leading zero if needed
                String formattedDay = String.format("%02d", dayOfMonth);  // Add leading zero if needed
                String selectedDate = year + "-" + formattedMonth + "-" + formattedDay;

                Intent intent = new Intent(History.this, HistoryItem.class);
                // Pass the selected date as an extra to the new activity
                intent.putExtra("SELECTED_DATE", selectedDate);
                // Start the new activity
                startActivity(intent);
            }
        });
        myDatabase = new MyDatabase(this);
        //Initializing and setting functionality for the bottom app navigation
        appNavigation = findViewById(R.id.historyBottomNavigation);
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem) {
                    intent = new Intent(History.this, MainActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.historyMenuItem) {

                }
                if (item.getItemId() == R.id.profileMenuItem) {
                    intent = new Intent(History.this, DogProfile.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
}

