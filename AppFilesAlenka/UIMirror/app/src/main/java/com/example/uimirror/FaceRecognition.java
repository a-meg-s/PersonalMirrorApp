package com.example.uimirror;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FaceRecognition {

    // Method to compare two sets of detected faces
    public static void compareDetectedFaces(List<Mat> detectedFaces1, List<Mat> detectedFaces2, Net torchAlgorithm) {
        for (int i = 0; i < detectedFaces1.size(); i++) {
            Mat face1 = detectedFaces1.get(i);
            for (int j = 0; j < detectedFaces2.size(); j++) {
                Mat face2 = detectedFaces2.get(j);
                double dist = compareFaces(torchAlgorithm, face1, face2);

                if (dist < 0.6) {
                    Log.d("FaceRecognition", "Compared Face " + (i + 1) + " of Image1 to Face " + (j + 1) + " of Image2. Distance: " + dist + " - Same Face.");
                } else if (dist < 0.9) {
                    Log.d("FaceRecognition", "Compared Face " + (i + 1) + " of Image1 to Face " + (j + 1) + " of Image2. Distance: " + dist + " - Different Face.");
                } else {
                    Log.d("FaceRecognition", "Compared Face " + (i + 1) + " of Image1 to Face " + (j + 1) + " of Image2. Distance: " + dist + " - Invalid comparison (likely not a face).");
                }
            }
        }
    }

    public static double compareFaces(Net torchAlgorithm, Mat face1, Mat face2) {
        // Load OpenFace model from assets

        if (torchAlgorithm.empty()) {
            Log.e("compareFaces", "Failed to load OpenFace model.");
            return -1; // Return error value if model fails to load
        }

        // Process both faces and get feature embeddings
        Mat feature1 = processFace(torchAlgorithm, face1);
        Mat feature2 = processFace(torchAlgorithm, face2);

        // Calculate and return the distance between the two embeddings
        return Core.norm(feature1, feature2);
    }


    // Process the cropped face with OpenFace model
    private static Mat processFace(Net net, Mat faceMat) {
        Log.d("processFace", "faceMat has this many channels: " + faceMat.channels());
        Mat inputBlob = Dnn.blobFromImage(faceMat, 1.0 / 255.0, new Size(96, 96), new org.opencv.core.Scalar(0, 0, 0), true, false);
        Log.d("processFace", "inputBlob has this many channels: " + inputBlob.channels());
        net.setInput(inputBlob);
        return net.forward().clone(); // Clone the result to avoid memory issues
    }

    public static String getTextfileFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            java.io.File tempFile = java.io.File.createTempFile(fileName, null, context.getCacheDir());
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
            Log.e("getTextfileFromAssets", "getAssetFilePath: IOException while loading file from assets.", e);
            return null;
        }
    }

    public static Bitmap loadImageFromAssets(Context context, String fileName) {
        Log.d("loadImageFromAssets", "loadImageFromAssets: Attempting to load image: " + fileName);
        try {
            InputStream is = context.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            // Rotate the bitmap if necessary
            bitmap = rotateImageIfNeeded(context, bitmap, fileName);
            return bitmap;
        } catch (IOException e) {
            Log.e("loadImageFromAssets", "loadImageFromAssets: IOException while loading image.", e);
            return null;
        }
    }

    // Rotate the image based on EXIF data
    public static Bitmap rotateImageIfNeeded(Context context, Bitmap img, String fileName) {
        try {
            ExifInterface exif = new ExifInterface(context.getAssets().open(fileName));
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
            Log.e("rotateImageIfNeeded", "rotateImageIfNeeded: IOException while reading EXIF data.", e);
            return img; // Return original if error occurs
        }
    }

    // Rotate the bitmap
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
