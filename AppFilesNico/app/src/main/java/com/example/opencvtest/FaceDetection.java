package com.example.opencvtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FaceDetection {

    public static List<Bitmap> detectAndCropFaceOpenCV(Context context, Bitmap bitmap) {
        Log.d("FaceDetection", "detectAndCropFaceOpenCV: Starting face detection.");
        Mat mat = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, mat);

        Log.d("FaceDetection", "detectAndCropFaceOpenCV: Converted Bitmap to Mat.");
        CascadeClassifier faceDetector = loadCascade(context, "haarcascade_frontalface_alt.xml");
        List<Bitmap> detectedFaces = new ArrayList<>();

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

                // Turn Normalized, cropped Face into Bitmap
                Bitmap croppedNormalizedBitmap = Bitmap.createBitmap(rect.width, rect.height, Bitmap.Config.ARGB_8888);
                org.opencv.android.Utils.matToBitmap(normalizedFace, croppedNormalizedBitmap);

                // Resize Bitmap
                Log.d("FaceDetection", "detectAndCropFaceOpenCV: Face cropped and normalized, resizing to 160x160.");
                Bitmap resizedFace = Bitmap.createScaledBitmap(croppedNormalizedBitmap, 160, 160, true);
                detectedFaces.add(resizedFace);
            }
        } else {
            Log.e("FaceDetection", "detectAndCropFaceOpenCV: Failed to load the face detector.");
        }
        return detectedFaces    ;
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
