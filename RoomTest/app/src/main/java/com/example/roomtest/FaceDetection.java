package com.example.roomtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static Mat loadImageFromAssets(Context context, String fileName) {
        Log.d("loadImageFromAssets", "loadImageFromAssets: Attempting to load image: " + fileName);
        try {
            InputStream is = context.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            // Rotate the bitmap if necessary
            bitmap = rotateImageIfNeeded(context, bitmap, fileName);

            //Convert bitmap to Mat
            Mat mat = new Mat();
            org.opencv.android.Utils.bitmapToMat(bitmap, mat);

            //Convert Colorspace from RGBA to BGR
            Mat threeChannelMat = new Mat();
            Imgproc.cvtColor(mat, threeChannelMat, Imgproc.COLOR_RGBA2BGR);

            return threeChannelMat;
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
    static void saveMatToFile(String path, Mat mat, String fileName) {
        // Convert Mat from BGR to RGB
        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB);

        // Convert RGB Mat to a Bitmap
        Bitmap bitmap = Bitmap.createBitmap(rgbMat.cols(), rgbMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbMat, bitmap);

        // Specify the path and file
        File file = new File(path, fileName);

        // Save the bitmap as JPEG
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            Log.d("CameraManager", "Image saved successfully: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("CameraManager", "Failed to save image: " + e.getMessage());
        }

        // Release the temporary Mat
        rgbMat.release();
    }

}
