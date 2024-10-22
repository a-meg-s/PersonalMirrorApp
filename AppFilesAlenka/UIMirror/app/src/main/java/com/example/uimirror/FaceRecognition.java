package com.example.uimirror;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class FaceRecognition {

    // Method to compare two sets of detected faces
    public static void compareDetectedFaces(List<Bitmap> detectedFaces1, List<Bitmap> detectedFaces2, String torchAlgorithm) {
        for (int i = 0; i < detectedFaces1.size(); i++) {
            Bitmap face1 = detectedFaces1.get(i);
            for (int j = 0; j < detectedFaces2.size(); j++) {
                Bitmap face2 = detectedFaces2.get(j);
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

    // Method to compare two individual faces
    public static double compareFaces(String torchAlgorithm, Bitmap face1, Bitmap face2) {

        // Convert cropped faces to Mat objects for processing with OpenCV
        Mat faceMat1 = bitmapToMat(face1);
        Mat faceMat2 = bitmapToMat(face2);

        // Load OpenFace model from assets
        Net net = Dnn.readNetFromTorch(torchAlgorithm);
        if (net.empty()) {
            Log.e("compareFaces", "Failed to load OpenFace model.");
            return -1; // Return error value if model fails to load
        }

        // Process both faces and get feature embeddings
        Mat feature1 = processFace(net, faceMat1);
        Mat feature2 = processFace(net, faceMat2);

        // Calculate and return the distance between the two embeddings
        return Core.norm(feature1, feature2);
    }

    // Convert Bitmap to Mat for OpenCV processing
    private static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB); // Convert to RGB if needed
        return mat;
    }

    // Process the cropped face with OpenFace model
    private static Mat processFace(Net net, Mat faceMat) {
        Mat inputBlob = Dnn.blobFromImage(faceMat, 1.0 / 255.0, new Size(96, 96), new org.opencv.core.Scalar(0, 0, 0), true, false);
        net.setInput(inputBlob);
        return net.forward().clone(); // Clone the result to avoid memory issues
    }
}
