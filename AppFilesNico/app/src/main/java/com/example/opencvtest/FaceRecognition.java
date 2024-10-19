package com.example.opencvtest;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

public class FaceRecognition {

    public static double compareFaces(String algorithm, Bitmap face1, Bitmap face2){

        // Convert cropped faces to Mat objects for processing with OpenCV
        Mat faceMat1 = bitmapToMat(face1);
        Mat faceMat2 = bitmapToMat(face2);

        // Load OpenFace model from assets
        Net net = Dnn.readNetFromTorch(algorithm);
        if (net.empty()) {
            Log.e("compareFaces", "Failed to load OpenFace model.");
        }

        // Process both faces and get feature embeddings
        Mat feature1 = processFace(net, faceMat1);
        Mat feature2 = processFace(net, faceMat2);

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
