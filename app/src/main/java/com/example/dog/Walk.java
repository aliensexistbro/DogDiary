package com.example.dog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Walk extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView stepsTextView;

    private Sensor temperatureSensor;

    private Sensor accelometer;

    private int stepCount = 0;
    private static final long TIMER_UPDATE_INTERVAL = 1000; // 1 second
    private long elapsedTime = 0;
    private Handler timerHandler;
    private Runnable timerRunnable;

    // Constants for step detection
    private static final float STEP_THRESHOLD = 12.0f; // Adjust this threshold based on your testing
    private static final int STEP_DELAY_NS = 250000000; // 250ms

    private long lastStepTimeNs = 0;
    private boolean isTracking = false;

    private TextView temperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk);

        stepsTextView = findViewById(R.id.stepsTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        // Get the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Replace "YOUR_API_KEY" with your OpenWeatherMap API key
        String apiKey = "942622e72df2c7325f9f997a8adc8a1a";
        String city = "TORONTO";
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=686a979a082d839e1167a81db85dfcf0";
        new FetchWeatherTask().execute(apiUrl);
        // Check if the step counter sensor is available
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        // Check if the temperature sensor is available
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (stepCounterSensor == null) {
            // Handle the case where the step counter sensor is not available on the device
            sensorManager.registerListener(this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        final Button startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    // Stop tracking
                    isTracking = false;
                    startStopButton.setText("Start");
                } else {
                    // Start tracking
                    isTracking = true;
                    startStopButton.setText("Stop");
                }
            }
        });
        //sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listener
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener to save battery when the activity is not in the foreground
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // Check if the event is from the step counter sensor
        if (isTracking) {
            if (stepCounterSensor == null) {
                detectStep(event.values[0], event.values[1], event.values[2], event.timestamp);
            } else {
                if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    // The event values contain the total number of steps taken since the last device reboot
                    stepCount = (int) event.values[0];
                    updateStepsView();
                }
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperatureCelsius = event.values[0];
            updateTemperatureView(temperatureCelsius);
        }
    }

    private void updateTemperatureView(float temperatureCelsius) {
        temperatureTextView.setText("Temperature: " + temperatureCelsius + "°C");
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

    private void updateStepsView() {
        stepsTextView.setText("Steps: " + stepCount);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL(params[0]);
                Log.d("Walk", params[0]);
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

