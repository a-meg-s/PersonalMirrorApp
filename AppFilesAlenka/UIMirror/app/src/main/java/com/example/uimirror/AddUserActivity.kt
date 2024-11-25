package com.example.uimirror

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Person
import kotlinx.coroutines.launch
import org.opencv.core.Core
import org.opencv.core.Core.ROTATE_90_COUNTERCLOCKWISE
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddUserActivity : AppCompatActivity() {
    private var faceDetected = false
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val detectedFaces = mutableListOf<Mat>() // List of detected faces
    private var selectedFaceIndex: Int? = null

    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var previewView: PreviewView
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputUserName: EditText
    private lateinit var buttonAddUser: Button
    private lateinit var buttonRetryDetection: Button

    // Kamera, Musik
    private lateinit var musicPlayer: MusicPlayer
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        musicPlayer = (applicationContext as MyApp).musicPlayer
        permissionHandler = PermissionHandler(this)

        Companion.loadModels(this)

        previewView = findViewById(R.id.previewView)
        previewView = findViewById(R.id.previewView)
        recyclerView = findViewById(R.id.recyclerViewFaces)
        inputUserName = findViewById(R.id.inputUserName)
        buttonAddUser = findViewById(R.id.buttonAddUser)
        buttonRetryDetection = findViewById(R.id.buttonRetryDetection)

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = FacesAdapter(detectedFaces, ::onFaceSelected)

        buttonAddUser.setOnClickListener { addUserToDatabase() }
        buttonRetryDetection.setOnClickListener { retryFaceDetection()

        }

        //startCamera()
        // Initialisiere Kamera und Permissionhandler (damit Preview funktioniert)
        cameraManager = CameraManager(this, findViewById(R.id.previewView), database, false)

        permissionHandler = PermissionHandler(this)

        // Kamera starten, wenn Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }
    }

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ).fallbackToDestructiveMigration()  // Daten werden bei jeder Versionsänderung gelöscht
            .build()
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

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView?.surfaceProvider)
            }
            imageAnalysis = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setTargetResolution(android.util.Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                if (!faceDetected) {  // Only process if no user has been detected
                    processImageProxy(imageProxy)
                } else {
                    imageProxy.close()
                }
            }


            try {
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraManager", "Failed to bind camera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

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
        detectAndCropFaces(rotatedMat)


        // Close the ImageProxy after processing
        imageProxy.close()
    }

    private fun detectAndCropFaces(mat: Mat) {
        val faces = FaceDetection.detectAndCropFaceOpenCV(mat, faceDetector)
        faces?.let {
            detectedFaces.clear()
            detectedFaces.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
    private fun onFaceSelected(index: Int) {
        selectedFaceIndex = index
        detectedFaces.retainAll { it == detectedFaces[index] }
        recyclerView.adapter?.notifyDataSetChanged()
    }
    private fun addUserToDatabase() {
        val userName = inputUserName.text.toString()
        if (userName.isBlank() || selectedFaceIndex == null) {
            Log.e("AddUser", "Invalid user data or no face selected.")
            return
        }

        val selectedFace = detectedFaces[selectedFaceIndex!!]
        // Convert Mat to a storable format (e.g., ByteArray)
        val faceData = matToByteArray(selectedFace)

        val newUser = Person(
            name = userName,
            faceData = faceData
        )

        lifecycle.coroutineScope.launch {
            database.uiMirrorDao().insertPerson(newUser)
        }
    }
    private fun retryFaceDetection() {
        detectedFaces.clear()
        recyclerView.adapter?.notifyDataSetChanged()
        faceDetected = false
    }
}