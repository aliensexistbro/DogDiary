package com.example.dog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FakeEntry extends AppCompatActivity {

    private EditText editTextDate, editTextPee, editTextPoop, editTextWalk, editTextStep, editTextStepTime, editTextFood;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fake_entry);

        // Initialize EditText fields
        editTextDate = findViewById(R.id.editTextDate);
        editTextPee = findViewById(R.id.editTextPee);
        editTextPoop = findViewById(R.id.editTextPoop);
        editTextWalk = findViewById(R.id.editTextBreed);
        editTextStep = findViewById(R.id.editTextStep);
        editTextStepTime = findViewById(R.id.editTextStepTime);
        editTextFood = findViewById(R.id.editTextFood);

        Button picBtn = findViewById(R.id.buttonPicture);

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FakeEntry.this, FakeCam.class);
                // Pass the selected date as an extra to the new activity
                intent.putExtra("SELECTED_DATE", editTextDate.getText().toString());
                // Start the new activity
                startActivity(intent);
            }
        });

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
            }
        });
    }

    private void saveDataToDatabase() {
        String date = editTextDate.getText().toString();
        int peeCount = getIntegerValue(editTextPee.getText().toString());
        int poopCount = getIntegerValue(editTextPoop.getText().toString());
        int walkCount = getIntegerValue(editTextWalk.getText().toString());
        int stepCount = getIntegerValue(editTextStep.getText().toString());
        int stepTime = getIntegerValue(editTextStepTime.getText().toString());
        int foodCount = getIntegerValue(editTextFood.getText().toString());
        String picUri = getIntent().getStringExtra("imageUri");

        MyDatabase myDatabase = new MyDatabase(this);
        // Check if the imageUri is empty or not provided
        if (TextUtils.isEmpty(picUri)) {
            // Set a default value or handle it as needed
            long id = myDatabase.insertPhotoData("none", date, poopCount,peeCount,foodCount, walkCount,stepCount,stepTime);
            if (id != -1) {
                Toast.makeText(this, "Data saved to database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving data to database", Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = myDatabase.insertPhotoData(picUri, date, poopCount,peeCount,foodCount, walkCount,stepCount,stepTime);
            if (id != -1) {
                Toast.makeText(this, "Data saved to database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving data to database", Toast.LENGTH_SHORT).show();
            }
        }

        // Initialize your MyDatabase instance

    }

    // Helper method to handle parsing integer values
    private int getIntegerValue(String stringValue) {
        if (TextUtils.isEmpty(stringValue)) {
            return 0; // Default value if the string is empty
        } else {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0; // Handle the case where parsing fails
            }
        }
    }

}
