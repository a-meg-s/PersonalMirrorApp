package com.example.uimirror

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.cardview.widget.CardView

class SettingsActivity : AppCompatActivity() {

    private lateinit var musicPlayer: MusicPlayer
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var cameraManager: CameraManager

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ).fallbackToDestructiveMigration() // Daten werden bei jeder Versionsänderung gelöscht
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        musicPlayer = MusicPlayer(this)
        permissionHandler = PermissionHandler(this)

        // UI-Elemente initialisieren
        val backToMainButton = findViewById<TextView>(R.id.backToMainButton)
        val addUserCard = findViewById<CardView>(R.id.addUserCard)
        val deactivateMusicCard = findViewById<CardView>(R.id.deactivateMusicCard)
        val deleteUserDataCard = findViewById<CardView>(R.id.deleteUserDataCard)

        val musicSwitch = findViewById<Switch>(R.id.musicSwitch)

        // Zurück-Button: Zurück zur Hauptaktivität
        backToMainButton.setOnClickListener {
            finish()
        }

        // Musik-Deaktivieren-Switch
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleSongPermission(isChecked)
        }

        // Karten für Benutzer hinzufügen
        addUserCard.setOnClickListener {
            // Benutzer hinzufügen Logik hier
            Toast.makeText(this, "Benutzer hinzufügen", Toast.LENGTH_SHORT).show()
        }

        // Karten für Benutzerdaten löschen
        deleteUserDataCard.setOnClickListener {
            // Benutzerdaten löschen Logik hier
            Toast.makeText(this, "Benutzerdaten löschen", Toast.LENGTH_SHORT).show()
        }

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

    private fun handleSongPermission(isChecked: Boolean) {
        if (isChecked) {
            musicPlayer.setMusicEnabled(true)
            Toast.makeText(this, "Hauptmusik aktiviert", Toast.LENGTH_SHORT).show()
        } else {
            musicPlayer.setMusicEnabled(false)
            musicPlayer.pauseMainSong()
            Toast.makeText(this, "Hauptmusik deaktiviert", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        // Hier kannst du die Berechtigungen überprüfen und den Zustand der Schalter aktualisieren
        updateSettingsState()
    }

    private fun updateSettingsState() {
        // Zustand der Berechtigungen aktualisieren
        val musicSwitch = findViewById<Switch>(R.id.musicSwitch)
        musicSwitch.isChecked = musicPlayer.isMusicEnabled()
    }
}
