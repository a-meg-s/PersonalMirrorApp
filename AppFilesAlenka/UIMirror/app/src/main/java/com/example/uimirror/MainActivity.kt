package com.example.uimirror

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uimirror.CameraManager
import com.example.uimirror.databinding.ActivityMainBinding
import com.example.uimirror.PermissionHandler

/*
// importierte Packete für die Funktion der App
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.content.Intent
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

// Import CameraX
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uimirror.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.activity.result.contract.ActivityResultContracts
*/


// MainActivity ist die Hauptklasse der App, die von AppCompatActivity erbt
class MainActivity : AppCompatActivity() {

    // Spätere Initialisierung der UI-Binding und Kamera-Executor Variablen
    private lateinit var binding: ActivityMainBinding // UI-Bindings für die Aktivität
    private lateinit var cameraManager: CameraManager //Instanz von CameraManager
    private lateinit var permissionHandler: PermissionHandler // Instanz von PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisiere die Klassen für Kamera und Berechtigungen
        cameraManager = CameraManager(this, binding.previewView)
        permissionHandler = PermissionHandler(this)

        // Berechtigungen beim Start überprüfen
        requestCameraPermissions()

        // Setze den OnClickListener für das Settings-Icon
        binding.settingsIcon?.setOnClickListener {
            // Zeigt den Dialog an, um zur App-Einstellungsseite weiterzuleiten
            permissionHandler.showPermissionDeniedDialog()
        }

        // Setze den OnClickListener für das Alarm-Icon
        binding.alarmIcon?.setOnClickListener {
            val intent = Intent(this, AlarmEditorActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestCameraPermissions() {
        // Überprüfen, ob Berechtigungen erteilt wurden
        if (permissionHandler.allPermissionsGranted()) {
            cameraManager.startCamera() // Starte die Kamera, wenn die Berechtigung erteilt wurde
        } else {
            // Fordert Berechtigungen an
            ActivityCompat.requestPermissions(this, PermissionHandler.REQUIRED_PERMISSIONS, PermissionHandler.REQUEST_CODE_PERMISSIONS)
            Toast.makeText(this, "Bitte erlauben Sie der App den Zugriff auf die Kamera in der nächsten Anfrage.", Toast.LENGTH_SHORT).show() // Kurze Meldung anzeigen
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
                Toast.makeText(this, "Kamerazugriff verweigert. Bitte aktivieren Sie ihn in den Einstellungen.", Toast.LENGTH_SHORT).show()
            }
        }
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
    }
}