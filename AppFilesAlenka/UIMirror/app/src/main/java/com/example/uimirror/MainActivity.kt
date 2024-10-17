package com.example.uimirror

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



// MainActivity ist die Hauptklasse der App, die von AppCompatActivity erbt
class MainActivity : AppCompatActivity() {

    // Spätere Initialisierung der UI-Binding und Kamera-Executor Variablen
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService

    // Create a launcher for notification permission request
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisiere das UI-Binding, um auf Layout-Elemente zuzugreifen
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Initialisiere den Kamera-Executor, der die Kameraoperationen in einem separaten Thread ausführt
        cameraExecutor = Executors.newSingleThreadExecutor()

       /* // Überprüfen, ob die Berechtigungen für die Kamera gewährt wurden
        if (allPermissionsGranted()) {
            // Wenn die Berechtigungen erteilt wurden, starte die Kamera
            startCamera()
        } else {
            // Wenn nicht, fordere die Kamera-Berechtigungen an
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }*/
        // Check notification permission
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Setze den OnClickListener für das Alarm-Icon
        binding.alarmIcon?.setOnClickListener {
            // Starte die AlarmEditorActivity
            val intent = Intent(this, AlarmEditorActivity::class.java)
            startActivity(intent)
        }

    }

    // überprüfe, ob alle benötigten Berechtigungen gewährt wurden
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Funktion zum Starten der Kamera
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Abrufen des Kamera-Providers, der die Kamera verwaltet
            val cameraProvider = cameraProviderFuture.get()

            // Erstellen der Kamera-Preview (Vorschau in der App)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView?.surfaceProvider)
            }
            try {
                // Kamera-Reset: Falls bereits eine Kamera aktiv ist, wird sie gestoppt (kann nur 1x verwendet werden)
                cameraProvider.unbindAll()

                // Kamera-Selector (Front Kamera)!!!!!!! HIER SCHAUEN AM GERAET
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Binde die Kamera und die Vorschau an den Lebenszyklus der Activity
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)

            } catch (exc: Exception) {
                Log.e("CameraBind", "Kamera-Bindung fehlgeschlagen: ${exc.message}")
            }
            // Kamera-Selector (Front Kamera)
            //val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA


            // Bind Preview und KameraLifecycle
           //cameraProvider.bindToLifecycle(this, cameraSelector, preview)

        }, ContextCompat.getMainExecutor(this))
    }

    // Verarbeite die Antwort auf die Berechtigungsanfrage & offnet direkt die Einstellungen
    /*override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Startet Kamera wenn Berechtigungen erteilt sind
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // Zeige eine Meldung an, dass die Berechtigung erforderlich ist
                Toast.makeText(
                    this,
                    "Kamerazugriff verweigert. Bitte aktivieren Sie die Berechtigung in den Einstellungen.",
                    Toast.LENGTH_LONG).show()

                // Öffnet die App-Einstellungen, damit der Benutzer die Berechtigungen manuell ändern kann
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
        }
    }*/
    // Funktion zur Verarbeitung des Ergebnisses der Berechtigungsanfrage
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // Wenn alle Berechtigungen gewährt wurden, starte die Kamera
                startCamera()
            } else {
                // Wenn nicht, zeige einen Dialog an, der erklärt, warum die Berechtigungen benötigt werden
                showPermissionDeniedDialog()
            }
        }
    }

    // Funktion zur Anzeige eines Dialogs, wenn die Berechtigungen abgelehnt wurden
    private fun showPermissionDeniedDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Kamerazugriff benötigt")
        builder.setMessage("Um diese Funktion zu nutzen, benötigt die App Zugriff auf die Kamera. Bitte aktivieren Sie die Berechtigung in den Einstellungen.")

        // Schaltfläche zum Öffnen der Einstellungen
        builder.setPositiveButton("Zu den Einstellungen") { dialog, _ ->
            dialog.dismiss()
            // Öffne die App-Einstellungen, damit der Benutzer die Berechtigungen manuell aktivieren kann
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }

        // Möglichkeit zum Abbrechen und zum Schliessen des Dialogs
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
            // Zeigt eine kurze Meldung an, dass der Kamerazugriff verweigert wurde
            Toast.makeText(this, "Kamerazugriff verweigert. Die Funktion ist eingeschränkt.", Toast.LENGTH_SHORT).show()
        }

        // Der Dialog kann nicht durch Tippen außerhalb des Dialogs geschlossen werden, zeigt Dialog an
        builder.setCancelable(false) // Der Dialog kann nicht durch Tippen außerhalb geschlossen werden
        builder.create().show()
    }

    // Methode wird aufgerufen, wenn die Activity in den Vordergrund tritt (z.B. nach dem Verlassen der Einstellungen)
    override fun onResume() {
        super.onResume()
        // Überprüfen, ob die Kamera-Berechtigung gewährt wurde, wenn der Benutzer zur App zurückkehrt
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            showPermissionDeniedDialog()
        }
    }


    // Begleitende Objekte für Berechtigungscode und erforderliche Berechtigungen
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown() // Executor beenden
    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10 // Konstante für die Anforderungs-ID der Berechtigungen
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA) // Array der benötigten Berechtigungen
    }
}
