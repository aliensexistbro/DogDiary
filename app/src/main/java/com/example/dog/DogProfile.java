package com.example.dog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class DogProfile extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    BottomNavigationView appNavigation;
    EditText nameET,birthdayET, ageET, breedET, cityET, chipET, weightET, furTypeET;
    Button saveProfileChange;

    RadioGroup chartSelect;
    private static final String PREF_NAME = "DogPrefs";

    ArrayList barChartEntries;
    ArrayList xLabels;
    BarData barData;
    BarDataSet barDataSet;
    BarChart barChart;


    public static final String DEFAULT = "not availiable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);

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

        barChart = findViewById(R.id.testBarChart);


        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.i("Shared Preferences: ", "Shared Prefs" + sharedPreferences.getString("name", DEFAULT));
        setDogInfo();

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



        MyDatabase database = new MyDatabase(this);
        String dataForPee = database.getDataFromColumn("pee");
        Toast.makeText(this, dataForPee, Toast.LENGTH_LONG).show();
//        createChart("step", "Number of steps");




    }


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
        editor.putFloat("weight", (float) weight); // using putFloat for double, as SharedPreferences doesn't support double
        editor.putString("furType", furType);

        editor.commit();
    }


    private void setDogInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.i("Shared Preferences: ", "Shared Prefs" + sharedPreferences.getString("name", DEFAULT));
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

    private void getBarEntries(String columnName){
        barChartEntries = new ArrayList<>();
        xLabels = new ArrayList<>();
        MyDatabase database = new MyDatabase(this);
        String dataColumn = database.getDataFromColumn(columnName);
        String[] data = dataColumn.split("\n");
        if(data.length > 0){
            for (int i = 0; i < data.length; i ++){
                String[] indiBarData = data[i].split(" ");
                xLabels.add(indiBarData[0]);
                barChartEntries.add(new BarEntry((float) i, Float.parseFloat(indiBarData[1])));
            }
        }

    }

    private void createChart (String columnName, String dataLabel){
        getBarEntries(columnName);
        barChart = findViewById(R.id.testBarChart);

        barDataSet = new BarDataSet(barChartEntries, dataLabel);
        barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group == chartSelect){
          if(checkedId == R.id.poopChartRadio){
              Toast.makeText(this, "poop checked", Toast.LENGTH_SHORT).show();
              barChart.invalidate();
              createChart("poo", "Number of Poops");

          }
          if(checkedId == R.id.peeChartRadio){
              Toast.makeText(this, "pee checked", Toast.LENGTH_SHORT).show();
              barChart.invalidate();
              createChart("pee", "Number of Pees");

          }
          if(checkedId == R.id.stepChartRadio){
              Toast.makeText(this, "steps checked", Toast.LENGTH_SHORT).show();
              barChart.invalidate();
              createChart("step", "Number of steps");

          }
        }

    }

    private void removeChart(){
        barChartEntries = null;
        barDataSet = null;
        barData = null;
        barChart.invalidate();
    }
}