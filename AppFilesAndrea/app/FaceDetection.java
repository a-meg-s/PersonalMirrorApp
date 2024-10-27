package com.example.uimirror;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

            Log.d("FaceDetection", "detectAndCropFaceOpenCV: Faces detected: " + faceDetections.toArray().length);
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


    public static CascadeClassifier loadCascade(Context context, String fileName) {
        try {
            // Load the cascade file from the application assets
            InputStream is = context.getAssets().open(fileName);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, fileName);

            // Write the file to a location accessible by OpenCV
            OutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the classifier from the copied file
            CascadeClassifier faceDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());

            // Delete the file after loading (optional)
            if (faceDetector.empty()) {
                faceDetector = null;
            }

            return faceDetector;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
