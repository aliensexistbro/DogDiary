package com.example.dog;

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
                }
            });
        }
}
