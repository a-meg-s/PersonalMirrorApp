package com.example.uimirror;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetManager {
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

    static void saveMatToFile(String path, Mat mat, String fileName) {
        // Convert Mat to a Bitmap
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);

        // Specify the path and file
        File file = new File(path, fileName);

        // Save the bitmap as JPEG
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            Log.d("CameraManager", "Image saved successfully: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("CameraManager", "Failed to save image: " + e.getMessage());
        }
    }


}
