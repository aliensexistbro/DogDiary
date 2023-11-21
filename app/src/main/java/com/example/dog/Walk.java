package com.example.dog;
import static com.example.dog.ActivityLog.getCurrentDate;
import static com.example.dog.ActivityLog.getCurrentTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Walk extends AppCompatActivity implements SensorEventListener {

    // Sensor-related variables
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView stepsTextView;
    private Sensor temperatureSensor;
    private Sensor accelometer;

    // Step tracking variables
    private int stepCount = 0;
    private static final long TIMER_UPDATE_INTERVAL = 1000; // 1 second
    private long elapsedTime = 0;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private static final float STEP_THRESHOLD = 12.0f; // Adjust this threshold based on your testing
    private static final int STEP_DELAY_NS = 250000000; // 250ms
    private long lastStepTimeNs = 0;
    private boolean isTracking = false;

    // UI elements
    private TextView temperatureTextView;
    private BottomNavigationView appNavigation;
    private boolean alert = false;
    private Sensor temperatureSensor2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk);

        stepsTextView = findViewById(R.id.stepsTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        // Get the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Check if the step counter sensor is available
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        // Check if the temperature sensor is available
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        temperatureSensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (stepCounterSensor == null) {
             //Handle the case where the step counter sensor is not available on the device
            sensorManager.registerListener(this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(temperatureSensor != null)
            sensorManager.registerListener(this, temperatureSensor,SensorManager.SENSOR_DELAY_NORMAL);
        else if (temperatureSensor2 != null)
        {
            sensorManager.registerListener(this, temperatureSensor2,SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // To be changed, getting information from the api
            String apiKey = "942622e72df2c7325f9f997a8adc8a1a";
            String city = "TORONTO";
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=686a979a082d839e1167a81db85dfcf0";
            new FetchWeatherTask().execute(apiUrl);
        }
        final Button startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    // Stop tracking
                    isTracking = false;
                    startStopButton.setText("Start Walk");
                    timerHandler.removeCallbacks(timerRunnable);
                } else {
                    // Start tracking
                    isTracking = true;
                    startStopButton.setText("End");
                    timerHandler.postDelayed(timerRunnable, TIMER_UPDATE_INTERVAL);
                }
            }
        });
        timerHandler = new Handler();
        timerRunnable = new Runnable() {  // Initialize timer for elapsed time tracking
            @Override
            public void run() {
                elapsedTime += TIMER_UPDATE_INTERVAL;
                updateElapsedTimeView();
                timerHandler.postDelayed(this, TIMER_UPDATE_INTERVAL);
            }
        };
        final Button endBtn = findViewById(R.id.endButton);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
                unregSensor();
                saveInfo();
                Intent intent = new Intent(Walk.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });

        appNavigation = findViewById(R.id.walkBottomNavigation);
        // Navigation bar
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem){
                    intent = new Intent(Walk.this, MainActivity.class);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.historyMenuItem){
                    intent = new Intent(Walk.this, History.class);
                    startActivity(intent);

                }
                if(item.getItemId() == R.id.profileMenuItem){
                    intent = new Intent(Walk.this, DogProfile.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    public void unregSensor()
    {
        sensorManager.unregisterListener(this);
    }

    private void saveInfo() // Saving information when walk ends
    {
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getColumnDataForToday();
        int hours = (int) TimeUnit.MILLISECONDS.toHours(elapsedTime);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) { // If there is already an entry
                cursor.moveToFirst();
                // Get the values
                @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex(Constants.WALK_COUNT)) +1;
                myDatabase.updateIntData(Constants.WALK_COUNT, getCurrentDate(), count);
                SharedPreferences sharedPreferences = getSharedPreferences("ActivityPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.WALK_COUNT, getCurrentTime());
                editor.apply();
                @SuppressLint("Range") int count2 = cursor.getInt(cursor.getColumnIndex(Constants.WALK_STEP_COUNT)) + stepCount;
                myDatabase.updateIntData(Constants.WALK_STEP_COUNT, getCurrentDate(), count2);
                @SuppressLint("Range") int count3 = cursor.getInt(cursor.getColumnIndex(Constants.WALK_TIME)) + hours;
                myDatabase.updateIntData(Constants.WALK_TIME, getCurrentDate(), count3);
            }
        } else { // Insert new data if there are no DB entries
            long id = myDatabase.insertPhotoData("none", getCurrentDate(), 0, 0, 0, 1, stepCount, hours);
            if (id != -1) {
                Toast.makeText(this, "Data saved to database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving data to database", Toast.LENGTH_SHORT).show();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    // Method to update the elapsed time view
    private void updateElapsedTimeView() {
        TextView elapsedTimeTextView = findViewById(R.id.timeTextView);
        elapsedTimeTextView.setText("Time: " + formatElapsedTime(elapsedTime));
    }

    private String formatElapsedTime(long elapsedTime) { // Formatting time elapsed
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listener
        if (accelometer != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener
        if (accelometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Check if the event is from the step counter sensor
        if (isTracking) {
            if (stepCounterSensor == null) { // Checks for step sensor, uses accelometer instead if null
                detectStep(event.values[0], event.values[1], event.values[2], event.timestamp);
            } else {
                if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    // The event values contain the total number of steps taken since the last device reboot
                    stepCount = (int) event.values[0];
                    updateStepsView();
                }
            }
        }
        // Check for temperature event
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE || event.sensor.getType() == Sensor.TYPE_TEMPERATURE) {
            float temperatureCelsius = event.values[0];
            updateTemperatureView(temperatureCelsius);
        }
    }

    private void updateTemperatureView(float temperatureCelsius) {
        temperatureTextView.setText("Temperature: " + temperatureCelsius + "°C");
        if(!alert) {
            if (temperatureCelsius <= 0 ) { // Show dialogue if bellow freezing
                // More than 5 minutes have elapsed
                if(elapsedTime >= TimeUnit.MINUTES.toMillis(5))
                    showDialog("Brr! It's cold out! Head back home soon.");
                else
                    showDialog("Brr! It's cold out! Make sure to have your dog wear a coat before leaving the house");
                alert = true;
            }
        }
    }

    private void showDialog(String message) { // Show dialogue if cold
        AlertDialog.Builder builder = new AlertDialog.Builder(Walk.this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        builder.create().show();
    }

    private void detectStep(float x, float y, float z, long timestamp) {
        // Calculate the magnitude of acceleration
        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
        // Check if acceleration exceeds the threshold and if enough time has passed since the last step
        if (acceleration > STEP_THRESHOLD && timestamp - lastStepTimeNs > STEP_DELAY_NS) {
            stepCount++;
            lastStepTimeNs = timestamp;
            updateStepsView();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    private void updateStepsView() { // Updating steps
        stepsTextView.setText("Steps: " + stepCount);
    }

    // To be changed
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL(params[0]);
                Log.d("Temp", params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Walk", s);
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");

                    // Convert temperature from Kelvin to Celsius
                    temperature -= 273.15;

                    updateTemperatureView((float) temperature);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case where the API request fails or returns null
            }
        }
    }
}

