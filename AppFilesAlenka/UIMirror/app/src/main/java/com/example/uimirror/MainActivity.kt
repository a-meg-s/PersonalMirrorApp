package com.example.uimirror

// importierte Packete für die Funktion der App
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uimirror.CameraManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.uimirror.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.example.uimirror.PermissionHandler
import org.opencv.android.OpenCVLoader
import android.media.MediaPlayer




// MainActivity ist die Hauptklasse der App, die von AppCompatActivity erbt
class MainActivity : AppCompatActivity() {

    // Spätere Initialisierung der UI-Binding und Kamera-Executor Variablen
    private lateinit var binding: ActivityMainBinding // UI-Bindings für die Aktivität
    private lateinit var cameraManager: CameraManager //Instanz von CameraManager
    private lateinit var permissionHandler: PermissionHandler // Instanz von PermissionHandler
    private lateinit var musicPlayer: MusicPlayer // Instanz von MusicPlayer

    // Getter-Methoden für den Zugriff im PermissionHandler
    fun getCameraManager(): CameraManager = cameraManager
    fun getBinding(): ActivityMainBinding = binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeOpenCV()
        initializeComponents()
        setupUIListeners()




        // Berechtigungen beim Start überprüfen
        //requestCameraPermissions()
        //permissionHandler.explainPermissionRationale()
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
        cameraManager = CameraManager(this, binding.previewView)
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
            permissionHandler.showPermissionCameraDeniedDialog()
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
        binding.calendarIcon?.setOnClickListener {
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
       // musicPlayer.playMainSong()// Überprüfen und abspielen des ausgewählten Songs bei rückkehr zur Activity
        if (musicPlayer.isMusicEnabled()) {
            musicPlayer.playMainSong()
        }
    }

    override fun onPause() {
        super.onPause()
      //  musicPlayer.pauseMainSong()
      //  musicPlayer.release()
    }

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

    /*private fun requestAndroidCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PermissionHandler.REQUEST_CODE_PERMISSIONS
            )
        } else {
            // Berechtigungen immer wieder neu anfragen, auch wenn sie erteilt sind
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PermissionHandler.REQUEST_CODE_PERMISSIONS
            )
        }
    }
}*/
/*
    private fun explainPermissionRationale() {
        // Zeige zuerst eine Erklärung, warum die Berechtigung benötigt wird
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Berechtigung erforderlich")
        builder.setMessage("Die Anwendung benötigt die Kameraberechtigung, um die Mirror- und Gesichtserkennung-Funktionalität zu ermöglichen. Ohne diese Berechtigung funktioniert die App nicht vollständig.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // Fordere die Berechtigung nach der Erklärung an
            requestCameraPermissions()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun requestCameraPermissions() {
        // Überprüfen, ob Berechtigungen erteilt wurden
        if (permissionHandler.allPermissionsGranted()) {
            cameraManager.startCamera() // Starte die Kamera, wenn die Berechtigung erteilt wurde
        } else {
            // Fordert Berechtigungen an
            //ActivityCompat.requestPermissions(this, PermissionHandler.REQUIRED_PERMISSIONS, PermissionHandler.REQUEST_CODE_PERMISSIONS)
           // Toast.makeText(this, "Bitte erlauben Sie der App den Zugriff auf die Kamera in der nächsten Anfrage.", Toast.LENGTH_SHORT).show() // Kurze Meldung anzeigen
            Snackbar.make(binding.root, "Bitte erlauben Sie den Kamerazugriff", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK") {
                    ActivityCompat.requestPermissions(this, PermissionHandler.REQUIRED_PERMISSIONS, PermissionHandler.REQUEST_CODE_PERMISSIONS)
                }
                .show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHandler.REQUEST_CODE_PERMISSIONS) {
            // Überprüfen, ob die Berechtigung erteilt wurde
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraManager.startCamera() // Starte die Kamera, wenn die Berechtigung erteilt wurde
            } else {
                // Kurze Meldung, wenn die Berechtigung abgelehnt wurde
                showPermissionDeniedDialog()
                Toast.makeText(this, "Kamerazugriff verweigert. Bitte aktivieren Sie ihn in den Einstellungen.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun showPermissionDeniedDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Berechtigung verweigert")
        builder.setMessage("Die Applikation benötigt die Kameraberechtigung. Bitte erteilen Sie die Berechtigung manuell in den Einstellungen.")
        builder.setPositiveButton("Zu den Einstellungen") { dialog, _ ->
            dialog.dismiss()
            openAppSettings()
        }
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
            finish() // App schließen oder eine Einschränkung anzeigen
        }
        builder.setCancelable(false)
        builder.show()
    }

    /*
    // Starte die Berechtigungsprüfung
    if (permissionHandler.allPermissionsGranted()) {
        cameraManager.startCamera() //startet die Kamera, wenn die Berechtigungen erteilt sind
    } else { // Fordert Berechtigungen an
        ActivityCompat.requestPermissions(this, PermissionHandler.REQUIRED_PERMISSIONS, PermissionHandler.REQUEST_CODE_PERMISSIONS)
    }

    // Benachrichtigungsberechtigung anfordern
   /* if (!permissionHandler.isNotificationPermissionGranted()) {
        permissionHandler.requestNotificationPermission()
    }*/

    // Setze den OnClickListener für das Alarm-Icon
    binding.alarmIcon?.setOnClickListener {
        val intent = Intent(this, AlarmEditorActivity::class.java)
        startActivity(intent) // Startet die Alarm-Editor-Aktivität
    }
}

    override fun onResume() {
        super.onResume()
        if (permissionHandler.allPermissionsGranted()) {
            cameraManager.startCamera() // Startet die Kamera bei der Wiederaufnahme, wenn Berechtigungen gewährt sind
        } else {
            permissionHandler.showPermissionDeniedDialog() // Zeigt den Dialog an, wenn die Berechtigungen verweigert wurden
        }
    }
    // Verarbeitet die Antwort auf die Berechtigungsanfrage
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHandler.REQUEST_CODE_PERMISSIONS) {
            if (permissionHandler.allPermissionsGranted()) {
                cameraManager.startCamera() // Startet die Kamera, wenn die Berechtigungen erteilt wurden
            } else {
                permissionHandler.showPermissionDeniedDialog() // Zeigt den Dialog an, wenn die Berechtigungen verweigert wurden
            }
        }
    } */
    override fun onResume() {
        super.onResume()
        // Überprüfen, ob die Berechtigungen erteilt wurden, um die Kamera zu starten
        if (permissionHandler.allPermissionsGranted()) {
            cameraManager.startCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown() // Stoppt die Kamera, wenn die Aktivität zerstört wird
    }*/
