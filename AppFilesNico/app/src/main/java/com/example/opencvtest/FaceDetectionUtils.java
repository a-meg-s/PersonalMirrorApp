package com.example.opencvtest;

import android.content.Context;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Environment;

public class FaceDetectionUtils {

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
