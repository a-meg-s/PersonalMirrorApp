package com.example.uimirror

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.objdetect.CascadeClassifier
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// This class manages the camera and its preview
class CameraManager(private val context: Context, private val previewView: PreviewView?) {

    init {
        // Ensure the models are loaded at initialization
        loadModels(context)
    }

    companion object {
        private var faceDetector: CascadeClassifier? = null
        private var faceRecognitionNet: Net? = null
        private var assetPhoto: Mat? = null

        // Load the models if they are not already loaded
        private fun loadModels(context: Context) {
            if (faceDetector == null) {
                faceDetector = FaceDetection.loadCascade(context, "haarcascade_frontalface_alt.xml")
                Log.d("CameraManager", "Cascade classifier loaded once.")
            }
            if (faceRecognitionNet == null) {
                faceRecognitionNet = Dnn.readNetFromTorch(FaceRecognition.getTextfileFromAssets(context, "nn4.small2.v1.t7"))
                Log.d("CameraManager", "OpenFace model loaded once.")
            }
            if (assetPhoto == null) {
                val bitmap = FaceRecognition.loadImageFromAssets(context, "Nico.jpg")
                if (bitmap != null) {
                    assetPhoto = Mat() // Initialize the Mat before using it
                    org.opencv.android.Utils.bitmapToMat(bitmap, assetPhoto)
                    Log.d("CameraManager", "AssetPhoto loaded once.")
                } else {
                    Log.e("CameraManager", "Failed to load bitmap from assets.")
                }
            }
        }

        // Provide access to the loaded face detector and recognition net
        fun getFaceDetector(): CascadeClassifier? = faceDetector
        fun getFaceRecognitionNet(): Net? = faceRecognitionNet
    }

    // Starts the camera
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView?.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                Log.e("CameraManager", "Failed to bind camera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private fun processImageProxy(imageProxy: ImageProxy) {
        val byteBuffer = imageProxy.planes[0].buffer
        val bytes = byteBuffer.toByteArray()

        // Convert byte array to OpenCV Mat
        val mat = Imgcodecs.imdecode(MatOfByte(*bytes), Imgcodecs.IMREAD_UNCHANGED)

        // Now pass the mat to your detection and recognition logic
        detectAndRecognizeFaces(mat)

        imageProxy.close() // Close the ImageProxy after processing
    }

    private fun detectAndRecognizeFaces(mat: Mat) {
        val faceDetector = getFaceDetector()
        val faceRecognitionNet = getFaceRecognitionNet()

        if (faceDetector != null && faceRecognitionNet != null) {
            val detectedFacesCamera = FaceDetection.detectAndCropFaceOpenCV(mat, faceDetector)
            val detectedFacesAssets = FaceDetection.detectAndCropFaceOpenCV(assetPhoto, faceDetector)

            FaceRecognition.compareDetectedFaces(detectedFacesCamera, detectedFacesAssets, faceRecognitionNet)

        } else {
            Log.e("CameraManager", "Face detector or recognition model is not loaded.")
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val byteArray = ByteArray(remaining())
        get(byteArray)
        return byteArray
    }

    // Stops the camera
    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
