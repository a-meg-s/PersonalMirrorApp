package com.example.uimirror

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uimirror.databinding.ActivityGreetingBinding

class GreetingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGreetingBinding
  //  private lateinit var cameraManager: CameraManager // Kamera-Manager Instanz
  //  private lateinit var permissionHandler: PermissionHandler // PermissionHandler Instanz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreetingBinding.inflate(layoutInflater) // Data Binding-Instanz für das Layout "login_view" erstellen
        setContentView(binding.root) // Setze den Inhalt der Activity auf die root-View des Bindings

/*
        cameraManager = CameraManager(this, binding.previewView) // Initialisiere den Kamera-Manager direkt mit `binding.previewView`
        permissionHandler = PermissionHandler(this) // Initialisiere den PermissionHandler für die Berechtigungsprüfung

        // Starte die Kamera, wenn die Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }*/

        //Beispiel text mit Name (später holen von Datenbank nach erkennung...)
        var recognizedName = "Alenka"
        // Aktuallisiert Begrüssungstext mit Name
        binding.greetingTextView.text = "Hallo, $recognizedName"

        // Setze einen OnTouchListener auf das gesamte Layout
        binding.root.setOnClickListener {
            // Wechsle zur MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: Schließe die LoginActivity, wenn du nicht mehr zurückkehren möchtest
            }

        }
    }
