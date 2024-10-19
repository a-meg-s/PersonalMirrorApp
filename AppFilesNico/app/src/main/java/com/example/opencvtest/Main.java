package com.example.opencvtest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

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

        // Initialize ImageView elements
        ImageView imageView1 = findViewById(R.id.croppedFaceView1);
        ImageView imageView2 = findViewById(R.id.croppedFaceView2);
        Log.d(TAG, "onCreate: ImageView initialized.");

        // Load images from assets
        Bitmap bitmap1 = assetUtils.loadImageFromAssets("Fabian2.jpg");
        Bitmap bitmap2 = assetUtils.loadImageFromAssets("Nico1.jpg");

        if (bitmap1 != null && bitmap2 != null) {
            Log.d(TAG, "onCreate: Images loaded successfully, starting face detection.");

            // Detect and crop faces from both images
            Bitmap croppedFace1 = FaceDetection.detectAndCropFaceOpenCV(this, bitmap1);
            Bitmap croppedFace2 = FaceDetection.detectAndCropFaceOpenCV(this, bitmap2);

            if (croppedFace1 != null && croppedFace2 != null) {
                Log.d(TAG, "onCreate: Faces detected and cropped, starting comparison.");

                // Display cropped faces in ImageViews
                imageView1.setImageBitmap(croppedFace1);
                imageView2.setImageBitmap(croppedFace2);

                double dist = FaceRecognition.compareFaces(faceRecognitionAlgorithm, croppedFace1, croppedFace2);

                if (dist < 0.6) {
                    Log.d(TAG, "Faces are the SAME! Distance: " + dist);
                } else {
                    Log.d(TAG, "Faces are DIFFERENT! Distance: " + dist);
                }

            } else {
                Log.e(TAG, "onCreate: No face detected in one or both images.");
            }
        } else {
            Log.e(TAG, "onCreate: Failed to load one or both images from assets.");
        }
    }
}

