package com.example.uimirror.musik

//Importiert
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.uimirror.CameraManager
import com.example.uimirror.PermissionHandler
import com.example.uimirror.R
import com.example.uimirror.database.PersonDatabase
import kotlinx.coroutines.launch

class SongSelectionActivity : AppCompatActivity() {
    private lateinit var musicPlayer: MusicPlayer
    private lateinit var adapter: SongAdapter
    private var currentPreviewButton: ImageButton? = null

    private lateinit var cameraManager: CameraManager // Hinzufügen der Kamera-Manager Instanz
    private lateinit var permissionHandler: PermissionHandler // Instanz von PermissionHandler

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ) .fallbackToDestructiveMigration()  // Daten werden bei jeder Versionsänderung gelöscht
            .build()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_selection)

        musicPlayer = MusicPlayer(this)
        setupSongList()

        // setzt Hintergrundfarbe des in DB ausgewählten Songs
        lifecycleScope.launch {
            val savedSongId = musicPlayer.loadSelectedSongId()
            savedSongId?.let {
                adapter.setInitialSelection(it)  // Wählt den gespeicherten Song aus und färbt den Hintergrund ein
            }
        }

        // Inizialisiert Kamera und Permissionhandler (damit Preview funktioniert)
        cameraManager = CameraManager(this, findViewById(R.id.previewView), database, false) // Initialisiere CameraManager mit PreviewView
        permissionHandler = PermissionHandler(this) // Initialisiere den PermissionHandler hier


        returnToMainActivity()
        deactivateMainSong()


        // Starte die Kamera, wenn die Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }

    }



    private fun setupSongList() {
        val songListView = findViewById<RecyclerView>(R.id.songListView)
        songListView.layoutManager = LinearLayoutManager(this)

        val songs = listOf(
            Song("Hey Brother", "Avicii", R.raw.hey_brother),
            Song("Rescue Me", "OneRepublic", R.raw.rescue_me),
            Song("Shivers", "Ed Sheeran", R.raw.shivers),
            Song("Bad Liars", "Imagine Dragons", R.raw.bad_liars)
        )

        adapter = SongAdapter(
            songs = songs,
            onPlayClick = { song, button -> onPlayClick(song, button) },
            onSongSelect = { song -> onSongSelected(song) }
        )
        songListView.adapter = adapter
    }

    private fun returnToMainActivity() {
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish() // Beendet die SongSelectionActivity und kehrt zur MainActivity zurück
        }
    }

    // In SongSelectionActivity
    private  fun deactivateMainSong() {
        val deactivateButton =  findViewById<Button>(R.id.deactivateMainSongButton)


        if(musicPlayer.isMusicEnabled()){
            deactivateButton.text = "Song deaktivieren" // Button-Text ändern
        } else{
            deactivateButton.text = "Song aktivieren" // Button-Text ändern
        }

        deactivateButton.setOnClickListener {
                // Überprüfen, ob Musik gerade läuft
                //lifecycleScope.launch {
                    if (musicPlayer.isMusicEnabled()) {
                        musicPlayer.pauseMainSong() // Musik pausieren
                        musicPlayer.setMusicEnabled(false) // Musik als deaktiviert markieren
                        deactivateButton.text = "Song aktivieren" // Button-Text ändern
                        //Toast.makeText(this, "Hauptmusik deaktiviert", Toast.LENGTH_SHORT).show()
                        Log.d("SongSelectionActivity", "deactivateMainSong: Deactivate Main Song button clicked")
                    } else {
                        musicPlayer.setMusicEnabled(true) // Musik als aktiviert markieren
                        deactivateButton.text = "Song deaktivieren" // Button-Text ändern
                        // Toast.makeText(this, "Hauptmusik aktiviert", Toast.LENGTH_SHORT).show()
                    }
                //}

        }
    }

    private fun onPlayClick(song: Song, button: ImageButton) {
        if (button == currentPreviewButton) {
            musicPlayer.stopPreview()
            updatePlayButton(button, false)
            currentPreviewButton = null
        } else {
            currentPreviewButton?.let { updatePlayButton(it, false) }
            musicPlayer.playPreview(song.resourceId)
            updatePlayButton(button, true)
            currentPreviewButton = button
        }
    }

    private fun updatePlayButton(button: ImageButton, isPlaying: Boolean) {
        button.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun onSongSelected(song: Song) {
        musicPlayer.saveSelectedSongId(song.resourceId)
        Toast.makeText(this, "Song '${song.name}' ausgewählt", Toast.LENGTH_SHORT).show()
        finish() // kehrt zur Main zurück
    }

    override fun onPause() {
        super.onPause()
        musicPlayer.stopPreview()
        currentPreviewButton?.let { updatePlayButton(it, false) }
        currentPreviewButton = null
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.release()
    }
}

data class Song(val name: String, val artist: String, val resourceId: Int)

