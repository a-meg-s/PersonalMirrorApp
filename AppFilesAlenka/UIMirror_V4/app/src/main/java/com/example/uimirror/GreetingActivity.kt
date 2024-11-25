package com.example.uimirror

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityGreetingBinding
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader

class GreetingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGreetingBinding
    private lateinit var cameraManager: CameraManager // Kamera-Manager Instanz
    private lateinit var permissionHandler: PermissionHandler // PermissionHandler Instanz
    private lateinit var musicPlayer: MusicPlayer // Instanz von MusicPlayer
    private var isCameraDialogShown = false


    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ) .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.coroutineScope.launch {
            addPersonsIfNeeded()
        }

        initializeOpenCV()
        initializeComponents()


       /* binding.root.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/

/*
        val recognizedName = "Alenka"
        binding.greetingTextView.text = "Hallo, $recognizedName"*/
    }

    private fun initializeOpenCV() {
        if (OpenCVLoader.initDebug()) {
            Log.i("GreetingActivity", "OpenCV loaded successfully")
        } else {
            Log.e("GreetingActivity", "OpenCV initialization failed!")
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeComponents() {
        cameraManager = CameraManager(this, binding.previewView, database, true)
        permissionHandler = PermissionHandler(this)

        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            isCameraDialogShown = true // Dialog wird angezeigt
            permissionHandler.showPermissionCameraDeniedDialog()
        }

    }

    // Methode zum Aktualisieren des Textes, wenn eine Person erkannt wird evtl.
    fun updateGreeting(personName: String?) {
        runOnUiThread {
            val greetingText = findViewById<TextView>(R.id.greetingTextView)

            // Show the appropriate greeting based on whether a name is detected
            greetingText.text = if (personName == "") {
                "Versucht Person zu erkennen"
            } else {
                "Hallo, $personName"
            }

            // Verzögerung für 5 Sekunden und dann zur MainActivity wechseln
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 5000) // 5000 Millisekunden = 5 Sekunden
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.handlePermissionsResult(requestCode, grantResults)
    }
    override fun onResume() {
        super.onResume()
        // Überprüfen, ob die Berechtigungen erteilt wurden, um die Kamera zu starten

       /* if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            // Berechtigungen fehlen, Dialog anzeigen
            permissionHandler.showPermissionCameraDeniedDialog()
        }*/

           if (permissionHandler.isCameraPermissionGranted()) {
               cameraManager.startCamera()
               isCameraDialogShown = false // Zurücksetzen, da die Berechtigung gewährt wurde
           } else if (!isCameraDialogShown) {
               isCameraDialogShown = true // Dialog wird angezeigt
               permissionHandler.showPermissionCameraDeniedDialog()
           }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown()
    }

    private suspend fun addPersonsIfNeeded() {
        //database.personDao().deleteAllPersons()
        val allPersons = getAllPersons()
        if (allPersons.isEmpty()) {
            Log.d("addPersonsIfNeeded", "Users added")
            val users = listOf(
                Person(id = 1, name = "Alenka", faceData = matToByteArray(AssetManager.loadImageFromAssets(this, "Alenka_Face.jpg"))),
                Person(id = 2, name = "Maria", faceData = matToByteArray(AssetManager.loadImageFromAssets(this, "Maria_Face.jpg"))),
                Person(id = 3, name = "Nico", faceData = matToByteArray(AssetManager.loadImageFromAssets(this, "Nico_Face.jpg"))),
                Person(id = 4, name = "Andrea", faceData = matToByteArray(AssetManager.loadImageFromAssets(this, "Andrea_Face.jpg")))
            )
            database.personDao().insertAll(users)
        }
    }

    private suspend fun getAllPersons(): List<Person> {
        val persons = database.personDao().getAllPersons()
        if (persons.isEmpty()) {
            Toast.makeText(this, "Inserting Users", Toast.LENGTH_SHORT).show()
        }
        return persons
    }
}