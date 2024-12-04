package com.example.uimirror

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.cardview.widget.CardView
import com.example.uimirror.musik.MusicPlayer
import com.example.uimirror.musik.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        musicPlayer = (applicationContext as MyApp).musicPlayer
        permissionHandler = PermissionHandler(this)

        // UI-Elemente initialisieren
        val backToMainButton = findViewById<TextView>(R.id.backToMainButton)
        val addUserCard = findViewById<CardView>(R.id.addUserCard)
        val deactivateMusicCard = findViewById<CardView>(R.id.deactivateMusicCard)
        val deleteUserDataCard = findViewById<CardView>(R.id.deleteUserDataCard)
        val deleteUserCard = findViewById<CardView>(R.id.deleteUserCard)

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
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Benutzer hinzufügen", Toast.LENGTH_SHORT).show()
        }

        // Karten für Benutzerdaten löschen
        deleteUserDataCard.setOnClickListener {
            // Benutzerdaten löschen Logik hier
             MaterialAlertDialogBuilder(this)
                 .setTitle("Reset")
                 .setMessage("Sind Sie sich ganz sicher das Sie ihre Daten löschen wollen? \n")
                 .setPositiveButton("reset") { dialog, _ ->
                     dialog.dismiss()
                     musicPlayer.pauseMainSong()
                     CoroutineScope(Dispatchers.Main).launch {
                         val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
                         primaryUser?.let{
                             database.uiMirrorDao().resetPrimaryUser(it.id)
                             it.events.clear()
                             //database.uiMirrorDao().updatePerson(it) //updatet es falsch?
                         }
                         Log.e("DeleteUser", "isPrimaryUser: ${primaryUser?.name}")

                     }
                     Toast.makeText(this, "Benutzerdaten wurden gelöscht", Toast.LENGTH_SHORT).show()

                 }
                 .setNegativeButton("Abbrechen") { dialog, _ ->
                     dialog.dismiss()
                 }
                 .setCancelable(false)
                 .show()
        }

        // User löschen
        deleteUserCard.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                 .setTitle("Daten löschen")
                 .setMessage("Sind Sie sich ganz sicher das Sie ihre Daten löschen wollen? \n Sobald Sie es bestätigen werden alle Daten über sie gelöscht inkl. ihrem Bild. \n Sie werden automatisch ausgeloggt und können sich nicht mehr einloggen, da Ihr Gesicht nicht mehr registriert ist. ")
                 .setPositiveButton("löschen") { dialog, _ ->
                     dialog.dismiss()
                     musicPlayer.pauseMainSong()
                     CoroutineScope(Dispatchers.Main).launch {
                         val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
                         database.uiMirrorDao().deletePrimaryUser()
                         Log.e("DeleteUser", "isPrimaryUser: ${primaryUser?.name}")
                     }
                     val intent = Intent(this, LoginActivity::class.java)
                     startActivity(intent)

                 }
                 .setNegativeButton("Abbrechen") { dialog, _ ->
                     dialog.dismiss()
                 }
                 .setCancelable(false)
                 .show()
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
            if (!musicPlayer.isMainPlaying()) {
                musicPlayer.playMainSong()
            }
            Toast.makeText(this, "Hauptmusik aktiviert", Toast.LENGTH_SHORT).show()
        } else {
            musicPlayer.pauseMainSong()
            musicPlayer.setMusicEnabled(false)
            Toast.makeText(this, "Hauptmusik deaktiviert", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        // Hier kannst du die Berechtigungen überprüfen und den Zustand der Schalter aktualisieren
        updateSettingsState()

        // Kamera starten, wenn Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }
    }

    private fun updateSettingsState() {
        // Zustand der Berechtigungen aktualisieren
        val musicSwitch = findViewById<Switch>(R.id.musicSwitch)
        musicSwitch.isChecked = musicPlayer.isMusicEnabled()
    }
}
