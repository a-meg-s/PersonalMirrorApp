package com.example.opencvtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class AssetUtils {

    private Context context;

    // Constructor to pass context
    public AssetUtils(Context context) {
        this.context = context;
    }

    // Helper method to load file from assets
    public String getTextfileFromAssets(String fileName) {
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

    // Load image from assets
    public Bitmap loadImageFromAssets(String fileName) {
        Log.d("loadImageFromAssets", "loadImageFromAssets: Attempting to load image: " + fileName);
        try {
            InputStream is = context.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            // Rotate the bitmap if necessary
            bitmap = rotateImageIfNeeded(bitmap, fileName);
            return bitmap;
        } catch (IOException e) {
            Log.e("loadImageFromAssets", "loadImageFromAssets: IOException while loading image.", e);
            return null;
        }
    }

    // Rotate the image based on EXIF data
    public Bitmap rotateImageIfNeeded(Bitmap img, String fileName) {
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
    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
