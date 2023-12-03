package com.example.dog;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class FakeCam  extends AppCompatActivity implements View.OnClickListener {

    // UI elements
    private PreviewView previewView;
    private ImageButton buttonCaptureSave;

    // Camera components
    private ImageCapture imageCapture;

    // Permission request code
    private final int REQUEST_CODE_PERMISSIONS = 1001;

    // Required camera permissions
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private String selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fake_cam);

        // Initialize UI elements
        buttonCaptureSave = findViewById(R.id.buttonCaptureSave);
        previewView = findViewById(R.id.previewView);
        buttonCaptureSave.setOnClickListener(this);

        // Check and request camera permissions
        if (allPermissionsGranted()) {
            startCamera(); // Start the camera if permissions have been granted
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        showToast(selectedDate);
    }

    // Helper method to get the executor
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    // Start the camera
    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }, getExecutor());
    }

    // Bind the camera preview
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonCaptureSave.getId()) {
            capturePhoto();
        }
    }

    // Capture a photo
    private void capturePhoto() {
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder( // Saving photo
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedImageUri = outputFileResults.getSavedUri();
                        String date = selectedDate; //Sending the uri and date to preview activity
                        Intent previewIntent = new Intent(FakeCam.this, FakeEntry.class);
                        previewIntent.putExtra("imageUri", savedImageUri.toString());
                        previewIntent.putExtra("date", date);
                        startActivity(previewIntent);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Handle image capture errors
                        Toast.makeText(FakeCam.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Check if all required permissions are granted
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
