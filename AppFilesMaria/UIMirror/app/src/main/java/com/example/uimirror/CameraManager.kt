package com.example.uimirror

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.geometry.Size
import androidx.core.content.ContextCompat
import com.example.uimirror.database.PersonDao
import com.example.uimirror.database.models.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.core.Core
import org.opencv.core.Core.ROTATE_90_COUNTERCLOCKWISE
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log

// This class manages the camera and its preview
class CameraManager(private val context: Context, private val previewView: PreviewView?) {
    private var cachedUserFaces: List<Mat>? = null
    private var cachedUserNames: List<String>? = null
    init {
        // Ensure the models are loaded at initialization
        loadModels(context)
        cachedUserFaces = cacheUserFaces()
        cachedUserNames = cacheUserNames()
    }

    companion object {
        private var faceDetector: CascadeClassifier? = null
        private var faceRecognitionNet: Net? = null
        private var assetPhoto: Mat? = null
        private var assetFaces: List<Mat>? = null

        // Load the models if they are not already loaded
        private fun loadModels(context: Context) {
            if (faceDetector == null) {
                faceDetector = AssetManager.loadCascade(context, "haarcascade_frontalface_alt.xml")
                Log.d("CameraManager", "Init: Cascade classifier loaded.")
            }
            if (faceRecognitionNet == null) {
                faceRecognitionNet = Dnn.readNetFromTorch(
                    AssetManager.getTextfileFromAssets(context,"nn4.small2.v1.t7")
                )
                Log.d("CameraManager", "Init: OpenFace model loaded.")
            }
            /*
            if (assetPhoto == null) {
                assetPhoto = AssetManager.loadImageFromAssets(context, "Nico.jpg")
                Log.d("CameraManager", "Init: Image from Assets loaded.")

                assetFaces = FaceDetection.detectAndCropFaceOpenCV(assetPhoto, faceDetector)
                Log.d("CameraManager", "Init: Faces from Assets detected.")
            }

             */
        }

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
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setTargetResolution(android.util.Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraManager", "Failed to bind camera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private fun processImageProxy(imageProxy: ImageProxy) {
        val yPlane = imageProxy.planes[0].buffer
        val uPlane = imageProxy.planes[1].buffer
        val vPlane = imageProxy.planes[2].buffer

        // Convert ByteBuffers to ByteArray
        val yBytes = ByteArray(yPlane.remaining())
        yPlane.get(yBytes)

        val uBytes = ByteArray(uPlane.remaining())
        val vBytes = ByteArray(vPlane.remaining())
        uPlane.get(uBytes)
        vPlane.get(vBytes)

        // Create a Mat for the Y plane
        val yMat = Mat(imageProxy.height, imageProxy.width, CvType.CV_8UC1)
        yMat.put(0, 0, yBytes)

        // Interleave U and V bytes to form a single UV plane
        val uvInterleaved = ByteArray(uBytes.size * 2)
        for (i in uBytes.indices) {
            uvInterleaved[i * 2] = uBytes[i]
            uvInterleaved[i * 2 + 1] = vBytes[i]
        }

        // Create a Mat for the UV plane
        val uvMat = Mat(imageProxy.height / 2, imageProxy.width / 2, CvType.CV_8UC2)
        uvMat.put(0, 0, uvInterleaved)

        // Convert YUV to BGR using cvtColorTwoPlane
        val bgrMat = Mat()
        Imgproc.cvtColorTwoPlane(yMat, uvMat, bgrMat, Imgproc.COLOR_YUV2BGR_NV21)

        //Log.d("processImageProxy", "bgrMat has this many Channels: " + bgrMat.channels().toString())

        val rotatedMat = Mat()

        if (bgrMat.empty()) {
            Log.e("CameraManager", "bgrMat is empty, check conversion.")
        } else {
            Log.d("CameraManager", "bgrMat is successfully created.")

            Core.rotate(bgrMat, rotatedMat, ROTATE_90_COUNTERCLOCKWISE)

           // Log.d("processImageProxy", "flippedBGRMat has this many Channels: " + flippedBGRMat.channels().toString() )

            // Save bgMat to external storage
           // AssetManager.saveMatToFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath, rotatedMat, "debugImage.jpg")
        }

        // Now pass the BGR Mat to your detection and recognition logic
        detectAndRecognizeFaces(rotatedMat)


        // Close the ImageProxy after processing
        imageProxy.close()
    }

    private fun detectAndRecognizeFaces(mat: Mat) {

       //  Log.d("detectAndRecognizeFaces", "mat has this many Channels: " + mat.channels().toString())
        // Log.d("detectAndRecognizeFaces", "assetPhoto has this many Channels: " + assetPhoto?.channels().toString())
        if (faceDetector != null && faceRecognitionNet != null) {
            val detectedFacesCamera = FaceDetection.detectAndCropFaceOpenCV(mat, faceDetector)

            FaceRecognition.compareDetectedFaces(
                detectedFacesCamera,
                cachedUserFaces,
                cachedUserNames,
                faceRecognitionNet
            )

        } else {
            Log.e("CameraManager", "Face detector or recognition model is not loaded.")
        }
    }
    private fun cacheUserFaces(): List<Mat> {
        val faceList = mutableListOf<Mat>()
        CoroutineScope(Dispatchers.IO).launch {
            val allUsers = UiMirrorApplication.database.personDao().getAllPersons()

            Log.d("CameraManager", "Cached ${allUsers.size} users from database.")

            faceList.addAll(allUsers.mapNotNull { user ->
                byteArrayToMat(user.faceData)
            })

            Log.d("CameraManager", "Collected faceData for ${faceList.size} users for Face extraction.")
        }
        return faceList
    }
    private fun cacheUserNames(): List<String> {
        val nameList = mutableListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val allUsers = UiMirrorApplication.database.personDao().getAllPersons()
            Log.d("CameraManager", "Cached ${allUsers.size} users from database for Name extraction.")

            nameList.addAll(allUsers.mapNotNull { user ->
                user.name
            })

            Log.d("CameraManager", "Collected faceData for ${nameList.size} users.")
        }
        return nameList
    }


    // Stops the camera
    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
