package com.example.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;

public class Main extends AppCompatActivity {
    private static final String TAG = "OpenCVTest";
    private ImageView imageView1; // for cropped face 1
    private ImageView imageView2; // for cropped face 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection); // Replace with your layout

        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV loaded successfully");
        } else {
            Log.e(TAG, "OpenCV initialization failed!");
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "onCreate: Activity created, setting content view.");

        // Initialize ImageView elements
        imageView1 = findViewById(R.id.croppedFaceView1);
        imageView2 = findViewById(R.id.croppedFaceView2);
        Log.d(TAG, "onCreate: ImageView initialized.");

        // Load images from assets
        Bitmap bitmap1 = loadImageFromAssets("Fabian2.jpg");
        Bitmap bitmap2 = loadImageFromAssets("Nico1.jpg");

        if (bitmap1 != null && bitmap2 != null) {
            Log.d(TAG, "onCreate: Images loaded successfully, starting face detection.");

            // Detect and crop faces from both images
            FaceDetection faceDetection = new FaceDetection();
            Bitmap croppedFace1 = faceDetection.detectAndCropFaceOpenCV(this, bitmap1);
            Bitmap croppedFace2 = faceDetection.detectAndCropFaceOpenCV(this, bitmap2);

            if (croppedFace1 != null && croppedFace2 != null) {
                Log.d(TAG, "onCreate: Faces detected and cropped, starting comparison.");

                // Display cropped faces in ImageViews
                imageView1.setImageBitmap(croppedFace1);
                imageView2.setImageBitmap(croppedFace2);

                // Convert cropped faces to Mat objects for processing with OpenCV
                Mat faceMat1 = bitmapToMat(croppedFace1);
                Mat faceMat2 = bitmapToMat(croppedFace2);

                // Load OpenFace model from assets
                Net net = Dnn.readNetFromTorch(getAssetFilePath("nn4.small2.v1.t7"));
                if (net.empty()) {
                    Log.e(TAG, "Failed to load OpenFace model.");
                    return;
                }

                // Process both faces and get feature embeddings
                Mat feature1 = processFace(net, faceMat1);
                Mat feature2 = processFace(net, faceMat2);

                // Compare embeddings using Euclidean distance
                double dist = Core.norm(feature1, feature2);
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

    // Convert Bitmap to Mat for OpenCV processing
    private Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB); // Convert to RGB if needed
        return mat;
    }

    // Process the cropped face with OpenFace model
    private Mat processFace(Net net, Mat faceMat) {
        Mat inputBlob = Dnn.blobFromImage(faceMat, 1.0 / 255.0, new Size(96, 96), new org.opencv.core.Scalar(0, 0, 0), true, false);
        net.setInput(inputBlob);
        return net.forward().clone(); // Clone the result to avoid memory issues
    }

    // Helper method to load file from assets
    private String getAssetFilePath(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            java.io.File tempFile = java.io.File.createTempFile(fileName, null, getCacheDir());
            java.io.FileOutputStream os = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "getAssetFilePath: IOException while loading file from assets.", e);
            return null;
        }
    }

    // Load image from assets
    private Bitmap loadImageFromAssets(String fileName) {
        Log.d(TAG, "loadImageFromAssets: Attempting to load image: " + fileName);
        try {
            InputStream is = getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            // Rotate the bitmap if necessary
            bitmap = rotateImageIfNeeded(bitmap, fileName);
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, "loadImageFromAssets: IOException while loading image.", e);
            return null;
        }
    }

    // Rotate the image based on EXIF data
    private Bitmap rotateImageIfNeeded(Bitmap img, String fileName) {
        try {
            ExifInterface exif = new ExifInterface(getAssets().open(fileName));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (IOException e) {
            Log.e(TAG, "rotateImageIfNeeded: IOException while reading EXIF data.", e);
            return img; // Return original if error occurs
        }
    }

    // Rotate the bitmap
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
