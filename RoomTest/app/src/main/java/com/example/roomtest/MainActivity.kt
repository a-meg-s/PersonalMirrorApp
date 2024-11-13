package com.example.roomtest

import android.content.pm.PackageManager
import android.media.FaceDetector.Face
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.objdetect.CascadeClassifier
import android.Manifest

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        }

        if (OpenCVLoader.initDebug()) {
            Log.i("MainActivity", "OpenCV loaded successfully");
        } else {
            Log.e("MainActivity", "OpenCV initialization failed!");
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show();
            return;
        }

        val faceDetector = FaceDetection.loadCascade(this, "haarcascade_frontalface_alt.xml")

        val facesNico = FaceDetection.detectAndCropFaceOpenCV(
            FaceDetection.loadImageFromAssets(this@MainActivity, "Alenka.jpg"),
            faceDetector
        )

        var i = 0
        for (face in facesNico) {
            // Adjust file path and name for each image
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
            val fileName = "Face_$i.jpg"

            FaceDetection.saveMatToFile(filePath, face, fileName)
            Log.d("Save Face", "Face saved")
            i++
        }

    }
}