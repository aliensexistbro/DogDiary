package com.example.dog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class DogInfo extends AppCompatActivity {

    private static final String PREF_NAME = "DogPrefs";

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
}

