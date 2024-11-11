package com.example.uimirror

// importierte Packete für die Funktion der App
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Music
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader


// MainActivity ist die Hauptklasse der App, die von AppCompatActivity erbt
class MainActivity : AppCompatActivity() {

    // Spätere Initialisierung der UI-Binding und Kamera-Executor Variablen
    private lateinit var binding: ActivityMainBinding // UI-Bindings für die Aktivität
    private lateinit var cameraManager: CameraManager //Instanz von CameraManager
    private lateinit var permissionHandler: PermissionHandler // Instanz von PermissionHandler
    private lateinit var musicPlayer: MusicPlayer // Instanz von MusicPlayer

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ) .fallbackToDestructiveMigration()  // Daten werden bei jeder Versionsänderung gelöscht
            .build()
    }

    // Getter-Methoden für den Zugriff im PermissionHandler
    fun getCameraManager(): CameraManager = cameraManager
    fun getBinding(): ActivityMainBinding = binding
    private lateinit var primaryUser: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeOpenCV()
        initializeComponents()
        setupUIListeners()


        // Berechtigungen beim Start überprüfen
        //requestCameraPermissions()
        if(!permissionHandler.isNotificationPermissionGranted()){
            permissionHandler.showPermissionNotificationDeniedDialog()
        }
        // Request storage permissions if not already granted
        if (!permissionHandler.isStoragePermissionGranted()) {
            permissionHandler.requestStoragePermissions()
        }
    }


    fun initializeOpenCV() {
        //Initialize OpenCVLibrary
        if (OpenCVLoader.initDebug()) {
            Log.i("MainActivity", "OpenCV loaded successfully");
        } else {
            Log.e("MainActivity", "OpenCV initialization failed!");
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private fun initializeComponents() {
        // Initialisiere die Klassen für Kamera und Berechtigungen
        cameraManager = CameraManager(this, binding.previewView, database)
        permissionHandler = PermissionHandler(this)
        musicPlayer = MusicPlayer(this) // Musikplayer initialisieren

        // Berechtigungen beim Start überprüfen
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            // Erklären warum Berechtigung benötigt
            permissionHandler.showPermissionCameraDeniedDialog()
        }
        //musicPlayer.playMainSong()
        if (musicPlayer.isMusicEnabled()) {
            musicPlayer.playMainSong()
        }
    }


    private fun setupUIListeners() {
        // Setze den OnClickListener für das Settings-Icon
        binding.settingsIcon?.setOnClickListener {
            // Zeigt den Dialog an, um zur App-Einstellungsseite weiterzuleiten
            //permissionHandler.showPermissionCameraDeniedDialog()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Setze den OnClickListener für das Alarm-Icon
        binding.alarmIcon?.setOnClickListener {
            val intent = Intent(this, AlarmEditorActivity::class.java)
            startActivity(intent)
        }
        // Setze den OnClickListener für das zurück zum Login (mail Icon)
        binding.mailIcon?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        // Setze den OnClickListener für das Kalender-Icon
        binding.playIcon?.setOnClickListener {
            musicPlayer.pauseMainSong()
            val intent = Intent(this, SongSelectionActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        // Überprüfen, ob die Berechtigungen erteilt wurden, um die Kamera zu starten
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            // Berechtigungen fehlen, Dialog anzeigen
            permissionHandler.showPermissionCameraDeniedDialog()
        }

        // Überprüfen und abspielen des ausgewählten Songs bei rückkehr zur Activity
        if(!musicPlayer.isMainPlaying()) {
            Log.i("MainActivity", "Music is playing");
            if(musicPlayer.isMusicEnabled()){
                Log.i("MainActivity", "Music is enabled");
                musicPlayer.playMainSong()
            }
        } else{
            Log.e("MainActivity", "Music not playing");
        }
    }

    override fun onPause() {
        super.onPause()
        //  musicPlayer.pauseMainSong()
        //  musicPlayer.release()
    }

    // wird von Android autom. aufgerufen, enn Berechtigungsanfrage bearbeitet wurde.
    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.handlePermissionsResult(requestCode, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown() // Stoppt die Kamera, wenn die Aktivität zerstört wird
        musicPlayer.release()
    }

    // Neue Methode zum Anzeigen einer Benachrichtigung
    fun showNotification(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

