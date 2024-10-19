package com.example.opencvtest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

import java.util.List;

public class Main extends AppCompatActivity {
    private static final String TAG = "OpenCVTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection); // Replace with your layout

        // Initialize AssetUtils here, after the activity is created
        AssetUtils assetUtils = new AssetUtils(this);

        // Load face recognition algorithm
        String faceRecognitionAlgorithm = assetUtils.getTextfileFromAssets("nn4.small2.v1.t7");

        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV loaded successfully");
        } else {
            Log.e(TAG, "OpenCV initialization failed!");
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "onCreate: Activity created, setting content view.");

        // Initialize ImageView containers (LinearLayouts for dynamic display of images)
        LinearLayout imageContainer1 = findViewById(R.id.imageContainer1);
        LinearLayout imageContainer2 = findViewById(R.id.imageContainer2);
        Log.d(TAG, "onCreate: Image containers initialized.");

        // Load images from assets
        Bitmap bitmap1 = assetUtils.loadImageFromAssets("Fabian2.jpg");
        Bitmap bitmap2 = assetUtils.loadImageFromAssets("Nico2.jpg");

        if (bitmap1 != null && bitmap2 != null) {
            Log.d(TAG, "onCreate: Images loaded successfully, starting face detection.");

            // Detect and crop faces from both images
            List<Bitmap> detectedFaces1 = FaceDetection.detectAndCropFaceOpenCV(this, bitmap1);
            List<Bitmap> detectedFaces2 = FaceDetection.detectAndCropFaceOpenCV(this, bitmap2);

            if (!detectedFaces1.isEmpty() && !detectedFaces2.isEmpty()) {
                Log.d(TAG, "onCreate: Faces detected, starting comparison.");

                // Display detected faces from bitmap1
                for (Bitmap face : detectedFaces1) {
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(face);
                    imageContainer1.addView(imageView);
                }

                // Display detected faces from bitmap2
                for (Bitmap face : detectedFaces2) {
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(face);
                    imageContainer2.addView(imageView);
                }

                // Compare each face in detectedFaces1 to each face in detectedFaces2
                FaceRecognition.compareDetectedFaces(detectedFaces1, detectedFaces2, faceRecognitionAlgorithm);

            } else {
                Log.e(TAG, "onCreate: No faces detected in one or both images.");
            }
        } else {
            Log.e(TAG, "onCreate: Failed to load one or both images from assets.");
        }
    }
}
