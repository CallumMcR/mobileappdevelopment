package com.example.calendarapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private ImageButton captureButton;
    private ImageButton saveButton;
    private ImageButton retakeButton;

    private String selectedDateText;
    private ImageView capturedImageView;
    private byte[] imageData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_main, container, false);

        // Find views in the layout
        surfaceView = rootView.findViewById(R.id.cameraPreview);
        captureButton = rootView.findViewById(R.id.captureButton);
        saveButton = rootView.findViewById(R.id.saveButton);
        retakeButton = rootView.findViewById(R.id.retakeButton);
        capturedImageView = rootView.findViewById(R.id.capturedImageView);

        // Set up the SurfaceHolder and its callback
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        // Set up capture button click listener
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        // Set up save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        // Set up retake button click listener
        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturedImageView.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                retakeButton.setVisibility(View.GONE);
                captureButton.setVisibility(View.VISIBLE);
                camera.startPreview();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check camera permission
        if (hasCameraPermission()) {
            // Camera permission is already granted
            openCamera();
        } else {
            // Request camera permission
            requestCameraPermission();
        }
    }


    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update the selected date text
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                selectedDateText = selectedDate;

                saveImageToDatabase(imageData, selectedDateText); // Save the image and selected date
                capturedImageView.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                retakeButton.setVisibility(View.GONE);
                captureButton.setVisibility(View.VISIBLE);
                camera.startPreview();
            }
        }, year, month, day);

        // Set the onCancelListener to return false when the dialog is canceled
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Return false when the dialog is canceled (user clicked "Cancel")
                selectedDateText = ""; // Clear the selected date text
                // Optionally, you can handle additional actions when the dialog is canceled
            }
        });

        // Show the DatePickerDialog
        datePickerDialog.show();
    }




    private boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check for camera permission on Android Marshmallow and above
            return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        // On versions prior to Marshmallow, camera permission is granted at install time
        return true;
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Explain why the camera permission is needed (optional)
            Toast.makeText(requireContext(), "Camera permission is required to capture images", Toast.LENGTH_SHORT).show();
        }

        // Request camera permission
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                openCamera();
            } else {
                // Camera permission denied
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        try {
            // Open the camera and set the preview display
            camera = Camera.open();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e("CameraFragment", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Check camera permission
        if (hasCameraPermission()) {
            // Camera permission is already granted
            openCamera();
        } else {
            // Request camera permission
            requestCameraPermission();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes, if needed
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Release the camera when the surface is destroyed
        releaseCamera();
    }

    private void captureImage() {
        // Set the picture callback
        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // Handle the captured image data here
                imageData = data;
                showCapturedImage();
            }
        };

        // Take the picture
        if (camera != null) {
            camera.takePicture(null, null, pictureCallback);
        }
    }

    private void showCapturedImage() {
        if (imageData != null) {
            // Display the captured image
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            capturedImageView.setImageBitmap(bitmap);
            capturedImageView.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            retakeButton.setVisibility(View.VISIBLE);
            captureButton.setVisibility(View.GONE);
        } else {
            Log.e("CameraFragment", "Null image data");
        }
    }



    private void saveImageToDatabase(byte[] imageData, String date) {
        // Save the image data to the database
        DBHandler dbHandler = new DBHandler(requireContext());
        dbHandler.addPhoto(imageData, date);
        Log.d("CameraFragment", "Image saved to database");
    }


    private void releaseCamera() {
        // Release the camera resources
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
