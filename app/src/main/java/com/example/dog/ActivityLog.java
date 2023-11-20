package com.example.dog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLog extends AppCompatActivity {
    private ImageButton walkBtn, foodBtn, poopBtn, peeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        walkBtn = findViewById(R.id.walkBtn);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to DogInfo activity
                Intent intent = new Intent(ActivityLog.this, Walk.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it with the back button
            }
        });
    }
}
