package com.example.dog;
import static com.example.dog.Constants.DATABASE_NAME;
import static com.example.dog.Constants.PHOTO_PATH;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class ImagePreviewActivity extends AppCompatActivity {

    private ImageView imageViewPreview;
    private EditText nameEditText;
    private Button saveBtn;
    private Button deleteBtn;
    private Button shareBtn;

    private Button homeBtn;
    private Uri imageUri;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);

        imageViewPreview = findViewById(R.id.image_preview);
        saveBtn = findViewById(R.id.imgPrevSaveButton);
        deleteBtn = findViewById(R.id.imgPrevDeleteButton);
        shareBtn = findViewById(R.id.imgPrevShareButton);
        homeBtn = findViewById(R.id.imgPrevHomeButton);
        // Get the image path and timestamp from the intent
        imageUri = getIntent().getParcelableExtra("imageUri");
        timestamp = getIntent().getStringExtra("date");

        // Load and display the image
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewPreview.setImageBitmap(bitmap);
                Log.d("ImagePreviewActivity", "Image loaded successfully");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ImagePreviewActivity", "Error loading image: " + e.getMessage());
                // Handle the exception
            }
        } else {
            Log.e("ImagePreviewActivity", "Image URI is null");
        }

        // Save button click listener
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDatabase();
            }
        });

        homeBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });
        // Delete button click listener
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
            }
        });

        // Share button click listener
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePhoto();
            }
        });
    }

    private void saveToDatabase() {
        // Get name from EditText
        // Save to database (replace this with your database handling logic)
        MyDatabase myDatabase = new MyDatabase(this);
        Cursor cursor = myDatabase.getPhotoDataForToday();
        if (cursor != null && cursor.getCount() > 0) {
            //(String oldTimestamp, String newPhotoPath, String newTimestamp)
            myDatabase.updateData(Constants.PHOTO_PATH, timestamp.toString(), imageUri.toString());
        } else {
            // Handle case where there is no data for today
            long id = myDatabase.insertPhotoData(imageUri.toString(), timestamp.toString(), 0,0,0, 0);
            if (id != -1) {
                Toast.makeText(this, "Data saved to database" + timestamp, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving data to database", Toast.LENGTH_SHORT).show();
            }
        }
        // Close the cursor to free up resources
        if (cursor != null) {
            cursor.close();
        }
        //String photoPath, String timestamp, String name

    }

    private void deletePhoto() {
        // Get the image ID from the content resolver
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] selectionArgs = {imageUri.getPath()};

        try (Cursor cursor = getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int imageIdColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(imageIdColumnIndex);

                // Delete the photo using the image ID
                Uri deleteUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
                int deletedRows = getContentResolver().delete(deleteUri, null, null);

                if (deletedRows > 0) {
                    Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after deleting the photo
                } else {
                    Toast.makeText(this, "Error deleting photo", Toast.LENGTH_SHORT).show();
                }
            }
        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    private void sharePhoto() {
        // Share the photo using Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}

