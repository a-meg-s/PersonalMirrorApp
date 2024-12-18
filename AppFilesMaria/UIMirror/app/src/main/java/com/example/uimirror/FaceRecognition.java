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
    public static int compareDetectedFaces(List<Mat> detectedFacesCamera, List<Mat> detectedFacesDatabase, List<String> namesDatabase, Net torchAlgorithm) {
        Log.d("compareDetectedFaces", "Number of detected faces in camera: " + detectedFacesCamera.size());
        Log.d("compareDetectedFaces", "Number of detected faces in database: " + detectedFacesDatabase.size());
        double dist = 0;

        for (int i = 0; i < detectedFacesCamera.size(); i++) {
            Mat face1 = detectedFacesCamera.get(i);
            for (int j = 0; j < detectedFacesDatabase.size(); j++) {
                Mat face2 = detectedFacesDatabase.get(j);
                dist = compareFaces(torchAlgorithm, face1, face2);

                if (dist < 0.6) {
                    Log.d("FaceRecognition", "Compared Face " + (i + 1) + " of Camera to Face " + (namesDatabase.get(j)) + " of Database. Distance: " + dist + " - Same Face.");
                    Log.d("FaceRecognition", "User found" + (namesDatabase.get(j)));
                    return j;
                } else if (dist < 0.9) {
                    Log.d("FaceRecognition", "Compared Face " + (i + 1) + " of Camera to Face " + (namesDatabase.get(j)) + " of Database. Distance: " + dist + " - Different Face.");
                } else {
                    Log.d("FaceRecognition", "Compared Face " + (i + 1) + " of Camera to Face " + (namesDatabase.get(j)) + " of Database. Distance: " + dist + " - Invalid comparison (likely not a face).");
                }
            }
        }

        return -1;

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
        //Log.d("processFace", "faceMat has this many channels: " + faceMat.channels());
        Mat inputBlob = Dnn.blobFromImage(faceMat, 1.0 / 255.0, new Size(96, 96), new org.opencv.core.Scalar(0, 0, 0), true, false);
       // Log.d("processFace", "inputBlob has this many channels: " + inputBlob.channels());
        net.setInput(inputBlob);
        return net.forward().clone(); // Clone the result to avoid memory issues
    }



}
