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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

public class DogProfile extends AppCompatActivity{
    BottomNavigationView appNavigation;
    EditText nameET,birthdayET, ageET, breedET, cityET, chipET, weightET, furTypeET;
    Button saveProfileChange;
    private static final String PREF_NAME = "DogPrefs";

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
}