package com.example.dog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class DogProfile extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    // UI Elements
    BottomNavigationView appNavigation;
    EditText nameET,birthdayET, ageET, breedET, cityET, chipET, weightET, furTypeET;
    Button saveProfileChange;
    TextView chartTitleTV;
    RadioGroup chartSelect;

    //Shared Preferences Strings
    private static final String PREF_NAME = "DogPrefs";
    public static final String DEFAULT = "not availiable";

    // Variables for the bar chart
    ArrayList barChartEntries;
    ArrayList xLabels;
    BarData barData;
    BarDataSet barDataSet;
    BarChart barChart;
    int daylimitor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);
        //Initializing the bar  UI elements
        appNavigation = findViewById(R.id.profileBottomNavigation);
        nameET = (EditText) findViewById(R.id.prEditTextName);
        birthdayET = (EditText) findViewById(R.id.prEditTextBirthday);
        ageET = (EditText) findViewById(R.id.prEditTextAge);
        breedET = (EditText) findViewById(R.id.prEditTextBreed);
        cityET = (EditText) findViewById(R.id.prEditTextCity);
        chipET = (EditText) findViewById(R.id.prEditTextChip);
        weightET = (EditText) findViewById(R.id.prEditTextWeight);
        furTypeET= (EditText) findViewById(R.id.prEditTextFurType);
        saveProfileChange = findViewById(R.id.saveProfileChangesButton);

        chartSelect = (RadioGroup) findViewById(R.id.chartSelect);
        chartSelect.setOnCheckedChangeListener(this);

        // Setting up bar chart
        barChart = findViewById(R.id.testBarChart);
        barChart.setVisibility(View.GONE);
        chartTitleTV = findViewById(R.id.chartTitle);


        setDogInfo();

        // Set the saving of the on click listener for the preferences.
        saveProfileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String birthday = birthdayET.getText().toString();
                int age = Integer.parseInt(ageET.getText().toString());
                String breed = breedET.getText().toString();
                String city = cityET.getText().toString();
                int chip = Integer.parseInt(chipET.getText().toString());
                double weight = Double.parseDouble(weightET.getText().toString());
                String furType = furTypeET.getText().toString();
                saveDogInfo(name, birthday, age, breed, city, chip, weight, furType);
            }
        });
        // Sets the bottom navigation for the app
        appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.homeMenuItem){
                    intent = new Intent(DogProfile.this, MainActivity.class);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.historyMenuItem){
                    intent = new Intent(DogProfile.this, History.class);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.profileMenuItem){
                }
                return true;
            }
        });

        // Assigns the number of data entries to display in the chart maximum
        daylimitor = 7;


    }

    // Saving the dog info to the preferences
    private void saveDogInfo(String name, String birthday, int age, String breed, String city,
                             int chip, double weight, String furType) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name", name);
        editor.putString("birthday", birthday);
        editor.putInt("age", age);
        editor.putString("breed", breed);
        editor.putString("city", city);
        editor.putInt("chip", chip);
        editor.putFloat("weight", (float) weight);
        editor.putString("furType", furType);

        editor.commit();
    }


    private void setDogInfo(){ //Setting dog's information
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            String name = sharedPreferences.getString("name", DEFAULT);
            String birthday = sharedPreferences.getString("birthday", DEFAULT);
            String breed = sharedPreferences.getString("breed", DEFAULT);
            String city = sharedPreferences.getString("city", DEFAULT);
            String furType = sharedPreferences.getString("furType", DEFAULT);
            int age = sharedPreferences.getInt("age", 0);
            int chip = sharedPreferences.getInt("chip", 0);
            float weight = sharedPreferences.getFloat("weight", 0);

            nameET.setText(name);
            birthdayET.setText(birthday);
            ageET.setText(String.valueOf(age));
            breedET.setText(breed);
            chipET.setText(String.valueOf(chip));
            cityET.setText(city);
            weightET.setText(String.valueOf(weight));
            furTypeET.setText(furType);
        }

    }
    // Gets the data for and puts it into a arraylists used to create the bar chart
    private void getBarEntries(String columnName){
        // Create new empty arraylists for the x labels and the dta
        barChartEntries = new ArrayList<>();
        xLabels = new ArrayList<>();
        // Gets reference to SQLite database
        MyDatabase database = new MyDatabase(this);
        // Tries to grab data from the database, upon an exception for no entries will send a toast that there is nothing for that column
        try {
            String dataColumn = database.getDataFromColumn(columnName);
            // Splits the data from the column into an array
            String[] data = dataColumn.split("\n");
            // Creates a new arraylist to store only the latest 7 days
            ArrayList<String> dataForWeek = new ArrayList<>();
            // Checks whether there is enough data to warrant limiting, if there isn't it displays all
            if(data.length < daylimitor) {
                daylimitor = data.length;
            }
            //Adds data in reverse order to arraylist.
            for (int j = daylimitor; j > 0; j --){
                int dataIndex = data.length - j;
                dataForWeek.add(data[dataIndex]);

            }
            // Adds a bar data entry for every item in the arraylist
            for (int i = 0; i < dataForWeek.size(); i ++){
                String[] indiBarData = dataForWeek.get(i).split(" ");
                xLabels.add(indiBarData[0]);
                barChartEntries.add(new BarEntry((float) i, Float.parseFloat(indiBarData[1])));
            }

        }
        catch (Exception e){
            // If no data is logged for that column the chart should not be visible
            Toast.makeText(this, "No Data Logged in this Column", Toast.LENGTH_SHORT).show();
            barChart.setVisibility(View.GONE);
        }
    }
    // Creates the bar chart using the data and the data labels
    private void createChart (String columnName, String dataLabel){
        getBarEntries(columnName);
        barChart = findViewById(R.id.testBarChart);
        //Checks if there is data in the arraylist
        if(barChartEntries.size() > 0){
            // Creates a Dataset from the arraylist
            barDataSet = new BarDataSet(barChartEntries, dataLabel);
            // Creates Data from the dataset to be used in the bar chart
            barData = new BarData(barDataSet);
            // Sets data to bar chart
            barChart.setData(barData);
            barChart.getDescription().setEnabled(false);
            barDataSet.setValueTextColor(R.color.black);
            XAxis xAxis = barChart.getXAxis();
            // Formats the X Axis to the dates
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
            // Makes chart Visible
            barChart.setVisibility(View.VISIBLE);

        }
    }

    // Radio buttons that controls what is displayed for the bar chart.
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group == chartSelect){
            // Sets bar chart to be invisible while the values change
            barChart.setVisibility(View.GONE);
          if(checkedId == R.id.poopChartRadio){
              barChart.invalidate();
              chartTitleTV.setText("Number of Poops");
              createChart("poo", "Number of Poops");
          }
          if(checkedId == R.id.peeChartRadio){
              barChart.invalidate();
              chartTitleTV.setText("Number of Pees");
              createChart("pee", "Number of Pees");
          }
          if(checkedId == R.id.stepChartRadio){
              barChart.invalidate();
              chartTitleTV.setText("Number of Steps");
              createChart("step", "Number of steps");
          }


        }

    }
}