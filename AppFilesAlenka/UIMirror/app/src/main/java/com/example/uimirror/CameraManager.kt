package com.example.uimirror

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.opencv.core.Core
import org.opencv.core.Core.ROTATE_180
import org.opencv.core.Core.ROTATE_90_COUNTERCLOCKWISE
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// This class manages the camera and its preview
class CameraManager(private val context: Context, private val previewView: PreviewView?, private val database: PersonDatabase, private val withImageAnalysis: Boolean = false) {
    private var userDetected = false
    private lateinit var imageAnalysis: ImageAnalysis
    private var allUsers: List<Person>? = null
    private var cachedUserFaces: List<Mat>? = null
    private var cachedUserNames: List<String>? = null

    private var detectedPerson: String? = null;

    init {
        // Ensure the models are loaded at initialization
        loadModels(context)
        allUsers = cacheUsers()
        cachedUserFaces = cacheUserFaces(allUsers)
        cachedUserNames = cacheUserNames(allUsers)
    }

    // Diese Methode setzt den erkannten Namen und informiert die GreetingActivity
    private fun setDetectedPerson(personName: String) {
        detectedPerson = personName
        // Wenn der Kontext die GreetingActivity ist, rufen wir die Methode zum Aktualisieren des Namens auf
        if (context is GreetingActivity) {
            (context as GreetingActivity).updateGreeting(detectedPerson)
        }
    }


    companion object {
        private var faceDetector: CascadeClassifier? = null
        private var faceRecognitionNet: Net? = null

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

            // Only create imageAnalysis if withImageAnalysis is true
            if (withImageAnalysis) {
                imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .setTargetResolution(android.util.Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    if (!userDetected) {  // Only process if no user has been detected
                        processImageProxy(imageProxy)
                    } else {
                        imageProxy.close()
                    }
                }
            }

            try {
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    cameraSelector,
                    preview
                )

                // Bind imageAnalysis only if needed
                if (withImageAnalysis) {
                    cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector,
                        imageAnalysis
                    )
                }
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

            //Rotation auf Tablet
            Core.rotate(bgrMat, rotatedMat, ROTATE_90_COUNTERCLOCKWISE)
            //Rotation auf Emulator:
            //Core.rotate(bgrMat, rotatedMat, ROTATE_180)

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

           detectedPerson = FaceRecognition.compareDetectedFaces(
                detectedFacesCamera,
                cachedUserFaces,
                cachedUserNames,
                faceRecognitionNet
            )
            detectedPerson?.let {
                userDetected = true
                updatePrimaryUser(allUsers, it)
                // Person wurde erkannt, also setze den Namen in der Activity
                setDetectedPerson(it)

                // Unbind image analysis to stop detection
                val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                ContextCompat.getMainExecutor(context).execute {
                    try {
                        cameraProvider.unbind(imageAnalysis)
                    } catch (exc: Exception) {
                        Log.e("CameraManager", "Error unbinding image analysis: ${exc.message}")
                    }
                }
            }

        } else {
            Log.e("CameraManager", "Face detector or recognition model is not loaded.")
        }
    }
    fun updatePrimaryUser(allUsers: List<Person>?, name: String){

        if (allUsers != null) {
            for (person in allUsers) {
                person.isPrimaryUser = (person.name == name)
            }

            // Save updated list to database
            CoroutineScope(Dispatchers.IO).launch {
                database.uiMirrorDao().insertAll(allUsers)
            }
        }

    }

    private fun cacheUsers(): List<Person>? {
        return runBlocking {
            val allUsers = database.uiMirrorDao().getAllPersons()
            Log.d("CameraManager", "Cached ${allUsers.size} users from database.")
            allUsers
        }
    }

    private fun cacheUserFaces(user: List<Person>?): List<Mat> {
        if (user == null) return emptyList()

        val faceList = user.mapNotNull { byteArrayToMat(it.faceData) }

        Log.d("CameraManager", "Collected faceData for ${faceList.size} users for Face extraction.")

        return faceList
    }

    private fun cacheUserNames(user: List<Person>?): List<String> {
        if (user == null) return emptyList()

        val nameList = user.mapNotNull { it.name }

        Log.d("CameraManager", "Collected names for ${nameList.size} users.")

        return nameList
    }

    // Stops the camera
    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
