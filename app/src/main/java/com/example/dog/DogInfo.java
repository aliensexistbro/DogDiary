package com.example.dog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DogInfo extends AppCompatActivity {

    private static final String PREF_NAME = "DogPrefs";
    BottomNavigationView appNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dog_info);

        Button submitButton = findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from EditText fields
                String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
                String birthday = ((EditText) findViewById(R.id.editTextBirthday)).getText().toString();
                int age = Integer.parseInt(((EditText) findViewById(R.id.editTextAge)).getText().toString());
                String breed = ((EditText) findViewById(R.id.editTextBreed)).getText().toString();
                String city = ((EditText) findViewById(R.id.editTextCity)).getText().toString();
                int chip = Integer.parseInt(((EditText) findViewById(R.id.editTextChip)).getText().toString());
                double weight = Double.parseDouble(((EditText) findViewById(R.id.editTextWeight)).getText().toString());
                String furType = ((EditText) findViewById(R.id.editTextFurType)).getText().toString();

                // Save the values in SharedPreferences
                saveDogInfo(name, birthday, age, breed, city, chip, weight, furType);
            }
        });

        if (hasDogInfo()){
            setDogInfo();
            appNavigation = findViewById(R.id.profileBottomNavigation);
            appNavigation.setVisibility(View.VISIBLE);
            appNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    if (item.getItemId() == R.id.homeMenuItem){
                        intent = new Intent(DogInfo.this, MainActivity.class);
                        startActivity(intent);
                    }
                    if(item.getItemId() == R.id.historyMenuItem){
                        intent = new Intent(DogInfo.this, History.class);
                        startActivity(intent);
                    }
                    if(item.getItemId() == R.id.profileMenuItem){
                    }
                    return true;
                }
            });


        }


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

        editor.apply();
        Intent intent = new Intent(DogInfo.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean hasDogInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Check if any key exists in SharedPreferences (indicating dog information)
        return sharedPreferences.getAll().size() > 0;
    }

    private void setDogInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        EditText name = (EditText) findViewById(R.id.editTextName);
        EditText birthday = (EditText) findViewById(R.id.editTextBirthday);
        EditText age = (EditText) findViewById(R.id.editTextAge);
        EditText breed = (EditText) findViewById(R.id.editTextBreed);
        EditText city = (EditText) findViewById(R.id.editTextCity);
        EditText chip = (EditText) findViewById(R.id.editTextChip);
        EditText weight = (EditText) findViewById(R.id.editTextWeight);
        EditText furType = (EditText) findViewById(R.id.editTextFurType);


        name.setText(sharedPreferences.getString("name", ""));
        birthday.setText(sharedPreferences.getString("birthday", ""));
        age.setText(String.valueOf(sharedPreferences.getInt("age", 0)));
        breed.setText(sharedPreferences.getString("breed", ""));
        chip.setText(String.valueOf(sharedPreferences.getInt("age", 0)));
        city.setText(sharedPreferences.getString("city", ""));

        furType.setText(sharedPreferences.getString("furType", ""));



    }
}

