package com.example.uimirror.biometrie;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;



public class FaceDetection {

    public static List<Mat> detectAndCropFaceOpenCV(Mat mat, CascadeClassifier faceDetector) {
        Log.d("FaceDetection", "detectAndCropFaceOpenCV: Starting face detection.");
        List<Mat> detectedFaces = new ArrayList<>();

        if (faceDetector != null) {
            Log.d("FaceDetection", "detectAndCropFaceOpenCV: Face detector loaded successfully.");
            // Detect Faces
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(mat, faceDetections);

            Log.d("FaceDetection", "detectAndCropFaceOpenCV: Nr. of Faces detected: " + faceDetections.toArray().length);
            for (Rect rect : faceDetections.toArray()) {
                // Crop the detected face from the image
                Mat croppedFace = new Mat(mat, rect);

                // Normalize Cropped Faces
                Mat normalizedFace = new Mat();
                Core.normalize(croppedFace, normalizedFace, 0, 255, Core.NORM_MINMAX);

                // Resize to 160x160
                Log.d("FaceDetection", "detectAndCropFaceOpenCV: Resizing face to 160x160.");
                Mat resizedFace = new Mat();
                Imgproc.resize(normalizedFace, resizedFace, new Size(160, 160));

                detectedFaces.add(resizedFace);
            }
        } else {
            Log.e("FaceDetection", "detectAndCropFaceOpenCV: Failed to load the face detector.");
        }
        return detectedFaces;
    }
    }
