package com.example.dog;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class History extends AppCompatActivity{
        private CalendarView calendarView;

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
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    Toast.makeText(History.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                    // Create an Intent to start the new activity
                    Intent intent = new Intent(History.this, HistoryItem.class);

                    // Pass the selected date as an extra to the new activity
                    intent.putExtra("SELECTED_DATE", selectedDate);

                    // Start the new activity
                    startActivity(intent);
                }
            });
        }
}
