package com.example.opencvtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetection {

    public Bitmap detectAndCropFaceOpenCV(Context context, Bitmap bitmap) {
        Log.d("FaceDetection", "detectAndCropFaceOpenCV: Starting face detection.");
        Mat mat = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, mat);

        Log.d("FaceDetection", "detectAndCropFaceOpenCV: Converted Bitmap to Mat.");
        CascadeClassifier faceDetector = FaceDetectionUtils.loadCascade(context, "haarcascade_frontalface_alt.xml");
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
                return Bitmap.createScaledBitmap(croppedNormalizedBitmap, 160, 160, true);
            }
        } else {
            Log.e("FaceDetection", "detectAndCropFaceOpenCV: Failed to load the face detector.");
        }
        return null;
    }
}
